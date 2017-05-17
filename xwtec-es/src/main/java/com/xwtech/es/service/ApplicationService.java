package com.xwtech.es.service;

import com.xwtech.es.ElasticConfig;
import com.xwtech.es.model.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentile;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesMethod;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangq on 2017/2/15.
 */
@Service
public class ApplicationService {

    static Logger logger = LoggerFactory.getLogger(ApplicationService.class);


    @Autowired
    TransportClient transportClient;

    @Autowired
    ElasticConfig elasticConfig;


    /**
     * 环境指标
     *
     * @param dateRange
     * @param host
     * @param port
     * @return
     */
    public List<EnvironmentBean> getEnvironmentAggregations(DateRange dateRange, String appCode, String host, String port) {

        List<EnvironmentBean> list = new ArrayList<EnvironmentBean>();

        //error 总数统计 日志包含 result_info 、 result_type ：分组子统计
        FilterAggregationBuilder filterAggregationBuilder =
                AggregationBuilders.filter("error",
                        QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("status", "0")));

        //统计时长 50线 95线 99线 ：分组子统计
        double[] p = new double[]{50, 95.0, 99.0};
        PercentilesAggregationBuilder percentiles =
                AggregationBuilders.percentiles("percentiles")
                        .field("interval")
                        .method(PercentilesMethod.HDR)
                        .numberOfSignificantValueDigits(5)
                        .percentiles(p);

        //统计时长 最大值 最小值 平均值 记录总数 ：分组子统计
        StatsAggregationBuilder statsAggregationBuilder =
                AggregationBuilders.stats("stats").field("interval");


        //组装分组
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        //定义分组的日期字段
        dateAgg.field("time");
        dateAgg.timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00")));
        //dateAgg.dateHistogramInterval(DateHistogramInterval.MINUTE);
        dateAgg.interval(dateRange.getInterval());
        dateAgg.format(dateRange.getFormat());

        //error 总数统计 日志包含 result_info 、 result_type ：分组子统计
        FilterAggregationBuilder filterAggregationBuilder1 =
                AggregationBuilders.filter("dateError",
                        QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("status", "0")));
        //统计 平均时长   请求数  错误数
        dateAgg.subAggregation(AggregationBuilders.count("dateCount").field("interval"));
        dateAgg.subAggregation(AggregationBuilders.avg("dateAvg").field("interval"));
        dateAgg.subAggregation(filterAggregationBuilder1);


        //根据 环境id分组 统计
        TermsAggregationBuilder termsAggregationBuilder =
                AggregationBuilders
                        .terms("environment")
                        .field("envir_ID.keyword")
                        .size(100)
                        .subAggregation(percentiles)
                        .subAggregation(statsAggregationBuilder)
                        .subAggregation(filterAggregationBuilder)
                        .subAggregation(dateAgg);


        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //事务日志的起始  日志不存在 last_node_ID   必要筛选条件
        queryBuilder = queryBuilder.mustNot(QueryBuilders.existsQuery("last_node_ID").queryName("exist"));

        long startTime = dateRange.getStart().getTime();
        long endTime = dateRange.getEnd().getTime();
        //时间间隔 毫秒数
        long interval = (endTime - startTime) / 1000;

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(dateRange.getStart().getTime())
                        .to(dateRange.getEnd().getTime())
        );


        //应用code 相当于  sdk-system
        queryBuilder = queryBuilder.must(QueryBuilders.termQuery("system", appCode));
        //.should(QueryBuilders.termQuery("customerId", currentCustomerId))
        //.should(QueryBuilders.termQuery("currentRole", ADMIN))
        //.minimumShouldMatch("1");


        //主机 可选条件
        if (!StringUtils.isEmpty(host)) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_ip", host));
            //端口 可选条件
            if (!StringUtils.isEmpty(port)) {
                queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_port", port));
            }
        }

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getTransactionType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(termsAggregationBuilder)
                        .setQuery(queryBuilder)
                        .addSort("interval", SortOrder.ASC).execute().actionGet();

        Aggregations aggregations1 = response.getAggregations();
        if (aggregations1 != null) {
            Terms terms = aggregations1.get("environment");
            for (Terms.Bucket bucket : terms.getBuckets()) {
                EnvironmentBean environmentBean = new EnvironmentBean();
                environmentBean.setEnvironmentId((String) bucket.getKey());

                logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());

                Aggregations aggregations = bucket.getAggregations();
                if (aggregations != null) {
                    Percentiles agg = aggregations.get("percentiles");

                    for (Percentile entry : agg) {
                        // Percent  // Value
                        double percent = entry.getPercent(), value = entry.getValue();

                        if (BigDecimal.valueOf(percent).compareTo(BigDecimal.valueOf(50)) == 0) {
                            environmentBean.setLine50(convert(value));
                        } else if (BigDecimal.valueOf(percent).compareTo(BigDecimal.valueOf(95)) == 0) {
                            environmentBean.setLine95(convert(value));

                        } else if (BigDecimal.valueOf(percent).compareTo(BigDecimal.valueOf(99)) == 0) {
                            environmentBean.setLine99(convert(value));
                        }
                        logger.debug("percent [{}], value [{}]", percent, convert(value));
                    }

                    //Min min = bucket.getAggregations().get("min");

                    //logger.debug("min:{}", convert(min.getValue()));

                    Stats stats = aggregations.get("stats");
                    double min = stats.getMin();
                    double max = stats.getMax();
                    double avg = stats.getAvg();
                    double sum = stats.getSum();
                    long count = stats.getCount();
                    environmentBean.setMin(convert(min));
                    environmentBean.setMax(convert(max));
                    environmentBean.setAvg(convert(avg));
                    environmentBean.setCount(count);

                    logger.debug("min:{},max:{},avg:{},sum:{},count:{}", convert(min), convert(max), convert(avg), convert(sum), (count));

                    Filter filter = aggregations.get("error");
                    logger.debug("error:{}", filter.getDocCount());
                    environmentBean.setErrorCount(filter.getDocCount());
                    environmentBean.setErrorRate(filter.getDocCount() / (double) count * 100);

                    environmentBean.setQbs(count / (double) interval);

                    //封装结果集
                    List<DateHistogramBean> dataList = new ArrayList<>();
                    InternalDateHistogram internalDateHistogram = aggregations.get("dateAgg");
                    for (Histogram.Bucket bucket1 : internalDateHistogram.getBuckets()) {
                        DateHistogramBean dateHistogramBean = new DateHistogramBean();
                        dateHistogramBean.setKey(bucket1.getKeyAsString());

                        logger.debug("{},{}", bucket1.getKeyAsString(), bucket1.getDocCount());

                        Aggregations aggregations2 = bucket1.getAggregations();
                        if (aggregations2 != null) {

                            Avg avg1 = aggregations2.get("dateAvg");

                            double avgValue = Double.isNaN(avg1.getValue()) ? 0d : avg1.getValue();

                            logger.debug("dateAvg : {}", avgValue);
                            dateHistogramBean.setAvg(convert(avgValue));

                            ValueCount valueCount = aggregations2.get("dateCount");
                            dateHistogramBean.setCount(valueCount.getValue());

                            Filter filter1 = aggregations2.get("dateError");
                            logger.debug("error:{}", filter1.getDocCount());

                            dateHistogramBean.setErrorCount(filter1.getDocCount());

                            dataList.add(dateHistogramBean);

                        }

                    }
                    environmentBean.setDateHistogramBeans(dataList);
                    logger.debug("{}", list);
                    list.add(environmentBean);

                }

            }
        }

        return list;

    }


    /**
     * //纳秒 转微秒 转毫秒
     *
     * @param interval 耗时 单位 纳秒
     * @return 毫秒
     */
    private double convert(double interval) {
        return (Math.floor(interval / 1000) / 1000);
    }


    /**
     * 获取查询索引列表 开始时间 - 结束时间
     *
     * @param start
     * @param end
     * @return 索引列表
     */
    String[] getIndices(Date start, Date end) {

        Calendar calendar = Calendar.getInstance();

        String[] dates = new String[]{};
        ArrayList<String> dateList = new ArrayList<>();
        Date date = start;
        while (date.compareTo(end) <= 0) {
            calendar.setTime(date);
            //业务处理...
            String temp = elasticConfig.getApplicationIndex() + DateFormatUtils.format(date, "yyyy.MM.dd") + "*";
            logger.debug("当前检索的es索引包含：{}", temp);
            dateList.add(temp);
            calendar.add(calendar.DATE, 1);
            date = calendar.getTime();
        }

        return dateList.toArray(dates);
    }

    /**
     * 判断指定的索引名是否存在
     *
     * @param indexName 索引名
     * @return 存在：true; 不存在：false;
     */
    public boolean isExistsIndex(String indexName) {
        IndicesExistsResponse response =
                transportClient.admin().indices().exists(
                        new IndicesExistsRequest().indices(new String[]{indexName})).actionGet();
        return response.isExists();
    }


    /**
     * 判断指定的索引的类型是否存在
     *
     * @param indexName 索引名
     * @param indexType 索引类型
     * @return 存在：true; 不存在：false;
     */
    public boolean isExistsType(String indexName, String indexType) {
        TypesExistsResponse response =
                transportClient.admin().indices()
                        .typesExists(new TypesExistsRequest(new String[]{indexName}, indexType)
                        ).actionGet();
        //System.out.println(FastJSONHelper.serialize(response));
        return response.isExists();
    }


    /**
     * 环境 服务节点指标
     *
     * @param dateRange
     * @param envId
     * @param host
     * @param port
     * @return
     */
    public List<NodeBean> getNodeAggregations(DateRange dateRange, String envId, String appCode, String host, String port) {

        List<NodeBean> list = new ArrayList<NodeBean>();

        //error 总数统计 日志包含 result_info 、 result_type ：分组子统计
        FilterAggregationBuilder filterAggregationBuilder =
                AggregationBuilders.filter("error",
                        QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("status", "0")));

        //统计时长 50线 95线 99线 ：分组子统计
        double[] p = new double[]{50, 95.0, 99.0};
        PercentilesAggregationBuilder percentiles =
                AggregationBuilders.percentiles("percentiles")
                        .field("interval")
                        .method(PercentilesMethod.HDR)
                        .numberOfSignificantValueDigits(5)
                        .percentiles(p);

        //统计时长 最大值 最小值 平均值 记录总数 ：分组子统计
        StatsAggregationBuilder statsAggregationBuilder =
                AggregationBuilders.stats("stats").field("interval");

        //组装分组
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        //定义分组的日期字段
        dateAgg.field("time");
        dateAgg.timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00")));
        //dateAgg.dateHistogramInterval(DateHistogramInterval.MINUTE);
        dateAgg.interval(dateRange.getInterval());
        dateAgg.format(dateRange.getFormat());


        //error 总数统计 日志包含 result_info 、 result_type ：分组子统计
        FilterAggregationBuilder filterAggregationBuilder1 =
                AggregationBuilders.filter("dateError",
                        QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("status", "0")));
        //统计 平均时长   请求数  错误数
        dateAgg.subAggregation(AggregationBuilders.count("dateCount").field("interval"));
        dateAgg.subAggregation(AggregationBuilders.avg("dateAvg").field("interval"));
        dateAgg.subAggregation(filterAggregationBuilder1);


        //根据 环境id分组 统计
        TermsAggregationBuilder termsAggregationBuilder =
                AggregationBuilders
                        .terms("node")
                        .field("node_code.keyword")
                        .size(1000)
                        .subAggregation(percentiles)
                        .subAggregation(statsAggregationBuilder)
                        .subAggregation(filterAggregationBuilder)
                        .subAggregation(dateAgg);


        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //事务日志的起始  日志不存在 last_node_ID   必要筛选条件
        queryBuilder = queryBuilder.mustNot(QueryBuilders.existsQuery("last_node_ID").queryName("exist"));

        //应用code 相当于  sdk-system
        queryBuilder = queryBuilder.must(QueryBuilders.termQuery("system", appCode));

        long startTime = dateRange.getStart().getTime();
        long endTime = dateRange.getEnd().getTime();
        //时间间隔 毫秒数
        long interval = (endTime - startTime) / 1000;

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(dateRange.getStart().getTime())
                        .to(dateRange.getEnd().getTime())
        );

        if (!StringUtils.isEmpty(envId)) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("envir_ID", envId));
        }

        //主机 可选条件
        if (!StringUtils.isEmpty(host)) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_ip", host));
            //端口 可选条件
            if (!StringUtils.isEmpty(port)) {
                queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_port", port));
            }
        }

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getTransactionType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(termsAggregationBuilder)
                        .setQuery(queryBuilder)
                        .addSort("interval", SortOrder.ASC).execute().actionGet();

        Aggregations aggregations1 = response.getAggregations();
        long a = response.getHits().getTotalHits();
        logger.debug("aaaa:{}", a);
        if (aggregations1 != null) {
            Terms terms = aggregations1.get("node");
            for (Terms.Bucket bucket : terms.getBuckets()) {
                NodeBean nodeBean = new NodeBean();
                nodeBean.setEnvironmentId(envId);
                nodeBean.setNodeCode((String) bucket.getKey());
                logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());

                Aggregations aggregations = bucket.getAggregations();
                if (aggregations != null) {
                    Percentiles agg = aggregations.get("percentiles");
                    for (Percentile entry : agg) {
                        double percent = entry.getPercent();    // Percent
                        double value = entry.getValue();        // Value
                        if (BigDecimal.valueOf(percent).compareTo(BigDecimal.valueOf(50)) == 0) {
                            nodeBean.setLine50(convert(value));
                        } else if (BigDecimal.valueOf(percent).compareTo(BigDecimal.valueOf(95)) == 0) {
                            nodeBean.setLine95(convert(value));

                        } else if (BigDecimal.valueOf(percent).compareTo(BigDecimal.valueOf(99)) == 0) {
                            nodeBean.setLine99(convert(value));
                        }
                        logger.debug("percent [{}], value [{}]", percent, convert(value));
                    }

                    Stats stats = aggregations.get("stats");
                    double min = stats.getMin();
                    double max = stats.getMax();
                    double avg = stats.getAvg();
                    double sum = stats.getSum();
                    long count = stats.getCount();
                    nodeBean.setMin(convert(min));
                    nodeBean.setMax(convert(max));
                    nodeBean.setAvg(convert(avg));
                    nodeBean.setCount(count);

                    logger.debug("min:{},max:{},avg:{},sum:{},count:{}", convert(min), convert(max), convert(avg), convert(sum), (count));

                    Filter filter = aggregations.get("error");
                    logger.debug("error:{}", filter.getDocCount());
                    nodeBean.setErrorCount(filter.getDocCount());
                    nodeBean.setErrorRate(filter.getDocCount() / (double) count * 100);

                    nodeBean.setQbs(count / (double) interval);
                    //todo
                    nodeBean.setRate(0);
                    logger.debug("{}", nodeBean);

                    //封装结果集
                    List<DateHistogramBean> dataList = new ArrayList<>();
                    InternalDateHistogram internalDateHistogram = aggregations.get("dateAgg");
                    for (Histogram.Bucket bucket1 : internalDateHistogram.getBuckets()) {
                        DateHistogramBean dateHistogramBean = new DateHistogramBean();
                        dateHistogramBean.setKey(bucket1.getKeyAsString());

                        logger.debug("{},{}", bucket1.getKeyAsString(), bucket1.getDocCount());

                        Aggregations aggregations2 = bucket1.getAggregations();
                        if (aggregations2 != null) {

                            Avg avg1 = aggregations2.get("dateAvg");

                            double avgValue = Double.isNaN(avg1.getValue()) ? 0d : avg1.getValue();

                            logger.debug("dateAvg : {}", avgValue);
                            dateHistogramBean.setAvg(convert(avgValue));

                            ValueCount valueCount = aggregations2.get("dateCount");
                            dateHistogramBean.setCount(valueCount.getValue());

                            Filter filter1 = aggregations2.get("dateError");
                            logger.debug("error:{}", filter1.getDocCount());

                            dateHistogramBean.setErrorCount(filter1.getDocCount());

                            dataList.add(dateHistogramBean);

                        }

                    }
                    nodeBean.setDateHistogramBeans(dataList);


                    list.add(nodeBean);

                }

            }
        }

        return list;

    }


    public Map<String, Object> getLogs(DateRange dateRange, String appCode, String host, String port, String key, int from, int size) {

        Map<String, Object> data = new HashMap<>();

        List<LogBean> list = new ArrayList<>();

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //事务日志的起始  日志不存在 last_node_ID   必要筛选条件
        queryBuilder = queryBuilder.mustNot(QueryBuilders.existsQuery("last_node_ID").queryName("exist"));

        long startTime = dateRange.getStart().getTime();
        long endTime = dateRange.getEnd().getTime();
        //时间间隔 毫秒数
        long interval = (endTime - startTime) / 1000;

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(dateRange.getStart().getTime())
                        .to(dateRange.getEnd().getTime())
        );


        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("system", appCode).operator(Operator.AND));

        //主机 可选条件
        if (!StringUtils.isEmpty(key)) {
            queryBuilder = queryBuilder.must(QueryBuilders.regexpQuery("custm_property",".*" + key + ".*"));
        }
        //主机 可选条件
        if (!StringUtils.isEmpty(host)) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_ip", host).operator(Operator.AND));
            //端口 可选条件
            if (!StringUtils.isEmpty(port)) {
                queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_port", port).operator(Operator.AND));
            }
        }

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getTransactionType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(queryBuilder).setFrom(from).setSize(size)
                        .addSort("time", SortOrder.DESC).execute().actionGet();

        data.put("total", response.getHits().getTotalHits());


        response.getHits().forEach(searchHitFields -> {
            logger.debug("{}", searchHitFields.getSourceAsString());

            LogBean logBean = new LogBean();
            Map map = searchHitFields.getSource();
            /**
             * 日志类型 当前日志的类型名称，取值：transaction
             */
            if (map.containsKey("log_type")) {
                Object object = map.get("log_type");
                if (object != null) {
                    logBean.setLog_type((String) object);
                }
            }

            /**
             * 日志结构版本 日志结构的版本号
             */
            if (map.containsKey("log_ver")) {
                Object object = map.get("log_ver");
                if (object != null) {
                    logBean.setLog_ver((String) object);
                }
            }

            /**
             * 应用名称 当前执行的系统名称，或者渠道名称
             */
            if (map.containsKey("system")) {
                Object object = map.get("system");
                if (object != null) {
                    logBean.setSystem((String) object);
                }
            }

            if (map.containsKey("last_system")) {
                Object object = map.get("last_system");
                if (object != null) {
                    logBean.setLast_system((String) object);
                }
            }


            /**
             * 系统版本号  当前应用的版本号
             */
            if (map.containsKey("ver")) {
                Object object = map.get("ver");
                if (object != null) {
                    logBean.setVer((String) object);
                }
            }

            /**
             * 微环境ID 当前事务所属微环境，可以作为识别某类事务的名称，比如某个活动的名称等，微环境可以包含一组不同的事务，缺省值为：default
             */
            if (map.containsKey("envir_ID")) {
                Object object = map.get("envir_ID");
                if (object != null) {
                    logBean.setEnvir_ID((String) object);
                }
            }


            /**
             * 关键时间 当前节点的执行时间
             */
            if (map.containsKey("time")) {
                Object object = map.get("time");
                if (object != null) {

                    Date date = DateTime.parse((String) object).toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00"))).toDate();

                    logBean.setTime(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS"));
                }
            }

            /**
             * 执行耗时 当前节点执行的总耗时纳秒数
             */
            if (map.containsKey("interval")) {
                Object object = map.get("interval");
                if (object != null) {
                    logBean.setInterval(convert((double) object));
                }
            }


            /**
             * 状态 当前节点执行后的返回码
             */
            if (map.containsKey("status")) {
                Object object = map.get("status");
                if (object != null) {
                    logBean.setStatus((String) object);
                }
            }


            /**
             * 返回信息类型 返回信息的类型，比如各种具体的异常类型等
             */

            if (map.containsKey("result_type")) {
                Object object = map.get("result_type");
                if (object != null) {
                    logBean.setResult_type((String) object);
                }
            }


            /**
             * 返回信息 当前节点执行后的返回的信息
             */
            if (map.containsKey("result_info")) {
                Object object = map.get("result_info");
                if (object != null) {
                    String info = (String) object;
                    logBean.setResult_info(info.replaceFirst("\u0003","").replaceAll("\u0003","<br />"));
                }
            }

            /**
             * 服务编码 当前一次针对服务的请求的ID
             */
            if (map.containsKey("service_ID")) {
                Object object = map.get("service_ID");
                if (object != null) {
                    logBean.setService_ID((String) object);
                }
            }


            /**
             * 当前节点ID 当前事务中，当前节点的ID
             */
            if (map.containsKey("node_ID")) {
                Object object = map.get("node_ID");
                if (object != null) {
                    logBean.setNode_ID((String) object);
                }
            }


            /**
             * 节点名称 当前节点的名称，也可以是方法名称等
             */
            if (map.containsKey("node_code")) {
                Object object = map.get("node_code");
                if (object != null) {
                    logBean.setNode_code((String) object);
                }
            }


            /**
             * 上级节点ID 当前事务中，上级节点的ID，若当前结点是根节点，则此为空
             */
            if (map.containsKey("last_node_ID")) {
                Object object = map.get("last_node_ID");
                if (object != null) {
                    logBean.setLast_node_ID((String) object);
                }
            }


            /**
             * 上级节点名称 上级节点的名称，也可以是方法名称等
             */
            if (map.containsKey("last_node_code")) {
                Object object = map.get("last_node_code");
                if (object != null) {
                    logBean.setLast_node_code((String) object);
                }
            }


            /**
             * 主机IP 当前节点执行的服务器地址
             */
            if (map.containsKey("server_ip")) {
                Object object = map.get("server_ip");
                if (object != null) {
                    logBean.setServer_ip((String) object);
                }
            }

            /**
             * 执行端口 当前节点执行的服务器端口
             */
            if (map.containsKey("server_port")) {
                Object object = map.get("server_port");
                if (object != null) {
                    logBean.setServer_port((String) object);
                }
            }

            /**
             * 调用模式
             */
            if (map.containsKey("mode")) {
                Object object = map.get("mode");
                if (object != null) {
                    logBean.setMode((String) object);
                }
            }

            /**
             * 客户端类型 Android|IOS|Server|Method
             */
            if (map.containsKey("client_type")) {
                Object object = map.get("client_type");
                if (object != null) {
                    logBean.setClient_type((String) object);
                }
            }

            /**
             * 客户端IP 客户端地址
             */
            if (map.containsKey("client_ip")) {
                Object object = map.get("client_ip");
                if (object != null) {
                    logBean.setClient_ip((String) object);
                }
            }

            /**
             * 客户端端口
             */
            if (map.containsKey("client_port")) {
                Object object = map.get("client_port");
                if (object != null) {
                    logBean.setClient_port((String) object);
                }
            }

            /**
             * 终端信息
             */

            if (map.containsKey("client_info")) {
                Object object = map.get("client_info");
                if (object != null) {
                    logBean.setClient_info((String) object);
                }
            }

            /**
             * 自定义属性 针对业务可自定义的属性集合，用字符”&“进行分割，属性是”key=value“结构
             */
            if (map.containsKey("custm_property")) {
                Object object = map.get("custm_property");
                if (object != null) {
                    logBean.setCustm_property((String) object);
                }
            }

            /**
             * 具体发起调用的方法节点ID
             */
            if (map.containsKey("invoke_node_ID")) {
                Object object = map.get("invoke_node_ID");
                if (object != null) {
                    logBean.setInvoke_node_ID((String) object);
                }
            }

            /**
             * 具体发起调用的方法名称
             */
            if (map.containsKey("invoke_node_code")) {
                Object object = map.get("invoke_node_code");
                if (object != null) {
                    logBean.setInvoke_node_code((String) object);
                }
            }

            list.add(logBean);
        });
        data.put("list", list);


        return data;
    }


    public List<LogBean> getLogs(String serviceId) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("service_ID", serviceId));

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(
                                elasticConfig.getTransactionType(),
                                elasticConfig.getChainType(),
                                elasticConfig.getMethodType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(queryBuilder).execute().actionGet();


        List<LogBean> logBeans = new ArrayList<>();
        logger.debug("{}", response.getHits().toString());

        for (SearchHit searchHit : response.getHits().getHits()) {

            logger.debug("{}", searchHit.getSourceAsString());

            LogBean logBean = new LogBean();
            Map map = searchHit.getSource();
            /**
             * 日志类型 当前日志的类型名称，取值：transaction
             */
            if (map.containsKey("log_type")) {
                Object object = map.get("log_type");
                if (object != null) {
                    logBean.setLog_type((String) object);
                }
            }

            /**
             * 日志结构版本 日志结构的版本号
             */
            if (map.containsKey("log_ver")) {
                Object object = map.get("log_ver");
                if (object != null) {
                    logBean.setLog_ver((String) object);
                }
            }

            /**
             * 应用名称 当前执行的系统名称，或者渠道名称
             */
            if (map.containsKey("system")) {
                Object object = map.get("system");
                if (object != null) {
                    logBean.setSystem((String) object);
                }
            }

            if (map.containsKey("last_system")) {
                Object object = map.get("last_system");
                if (object != null) {
                    logBean.setLast_system((String) object);
                }
            }


            /**
             * 系统版本号  当前应用的版本号
             */
            if (map.containsKey("ver")) {
                Object object = map.get("ver");
                if (object != null) {
                    logBean.setVer((String) object);
                }
            }

            /**
             * 微环境ID 当前事务所属微环境，可以作为识别某类事务的名称，比如某个活动的名称等，微环境可以包含一组不同的事务，缺省值为：default
             */
            if (map.containsKey("envir_ID")) {
                Object object = map.get("envir_ID");
                if (object != null) {
                    logBean.setEnvir_ID((String) object);
                }
            }


            /**
             * 关键时间 当前节点的执行时间
             */
            if (map.containsKey("time")) {
                Object object = map.get("time");
                if (object != null) {

                    Date date = DateTime.parse((String) object).toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00"))).toDate();

                    logBean.setTime(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS"));
                }
            }


            /**
             * 执行耗时 当前节点执行的总耗时纳秒数
             */
            if (map.containsKey("interval")) {
                Object object = map.get("interval");
                if (object != null) {
                    logBean.setInterval(convert((double) object));
                }
            }


            /**
             * 状态 当前节点执行后的返回码
             */
            if (map.containsKey("status")) {
                Object object = map.get("status");
                if (object != null) {
                    logBean.setStatus((String) object);
                }
            }


            /**
             * 返回信息类型 返回信息的类型，比如各种具体的异常类型等
             */

            if (map.containsKey("result_type")) {
                Object object = map.get("result_type");
                if (object != null) {
                    logBean.setResult_type((String) object);
                }
            }


            /**
             * 返回信息 当前节点执行后的返回的信息
             */
            if (map.containsKey("result_info")) {
                Object object = map.get("result_info");
                if (object != null) {
                    String info = (String) object;
                    logBean.setResult_info(info.replaceFirst("\u0003","").replaceAll("\u0003","<br />"));
                }
            }

            /**
             * 服务编码 当前一次针对服务的请求的ID
             */
            if (map.containsKey("service_ID")) {
                Object object = map.get("service_ID");
                if (object != null) {
                    logBean.setService_ID((String) object);
                }
            }


            /**
             * 当前节点ID 当前事务中，当前节点的ID
             */
            if (map.containsKey("node_ID")) {
                Object object = map.get("node_ID");
                if (object != null) {
                    logBean.setNode_ID((String) object);
                }
            }


            /**
             * 节点名称 当前节点的名称，也可以是方法名称等
             */
            if (map.containsKey("node_code")) {
                Object object = map.get("node_code");
                if (object != null) {
                    logBean.setNode_code((String) object);
                }
            }


            /**
             * 上级节点ID 当前事务中，上级节点的ID，若当前结点是根节点，则此为空
             */
            if (map.containsKey("last_node_ID")) {
                Object object = map.get("last_node_ID");
                if (object != null) {
                    logBean.setLast_node_ID((String) object);
                }
            }


            /**
             * 上级节点名称 上级节点的名称，也可以是方法名称等
             */
            if (map.containsKey("last_node_code")) {
                Object object = map.get("last_node_code");
                if (object != null) {
                    logBean.setLast_node_code((String) object);
                }
            }


            /**
             * 主机IP 当前节点执行的服务器地址
             */
            if (map.containsKey("server_ip")) {
                Object object = map.get("server_ip");
                if (object != null) {
                    logBean.setServer_ip((String) object);
                }
            }

            /**
             * 执行端口 当前节点执行的服务器端口
             */
            if (map.containsKey("server_port")) {
                Object object = map.get("server_port");
                if (object != null) {
                    logBean.setServer_port((String) object);
                }
            }

            /**
             * 调用模式
             */
            if (map.containsKey("mode")) {
                Object object = map.get("mode");
                if (object != null) {
                    logBean.setMode((String) object);
                }
            }

            /**
             * 客户端类型 Android|IOS|Server|Method
             */
            if (map.containsKey("client_type")) {
                Object object = map.get("client_type");
                if (object != null) {
                    logBean.setClient_type((String) object);
                }
            }

            /**
             * 客户端IP 客户端地址
             */
            if (map.containsKey("client_ip")) {
                Object object = map.get("client_ip");
                if (object != null) {
                    logBean.setClient_ip((String) object);
                }
            }

            /**
             * 客户端端口
             */
            if (map.containsKey("client_port")) {
                Object object = map.get("client_port");
                if (object != null) {
                    logBean.setClient_port((String) object);
                }
            }

            /**
             * 终端信息
             */

            if (map.containsKey("client_info")) {
                Object object = map.get("client_info");
                if (object != null) {
                    logBean.setClient_info((String) object);
                }
            }

            /**
             * 自定义属性 针对业务可自定义的属性集合，用字符”&“进行分割，属性是”key=value“结构
             */
            if (map.containsKey("custm_property")) {
                Object object = map.get("custm_property");
                if (object != null) {
                    logBean.setCustm_property((String) object);
                }
            }

            /**
             * 具体发起调用的方法节点ID
             */
            if (map.containsKey("invoke_node_ID")) {
                Object object = map.get("invoke_node_ID");
                if (object != null) {
                    logBean.setInvoke_node_ID((String) object);
                }
            }

            /**
             * 具体发起调用的方法名称
             */
            if (map.containsKey("invoke_node_code")) {
                Object object = map.get("invoke_node_code");
                if (object != null) {
                    logBean.setInvoke_node_code((String) object);
                }
            }

            logBeans.add(logBean);
        }

        return logBeans;
    }


    /**
     * 获取奔溃总数统计信息
     * @param
     * @return
     */
    public Map<String,Object> getCrashDetails(DateRange dateRange,
                                                  String techType,
                                                  String ver,
                                                  String key,
                                                  int from,
                                                  int size) {

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(dateRange.getStart().getTime())
                        .to(dateRange.getEnd().getTime())
        );

        queryBuilder = queryBuilder
                .must(QueryBuilders.matchQuery("techType", techType))
                .must(QueryBuilders.matchQuery("ver", ver));

        if(!StringUtils.isEmpty(key)){
            queryBuilder = queryBuilder.must(QueryBuilders.regexpQuery("module",".*" + key + ".*"));
        }

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getAppcrash())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setSize(size)
                        .setFrom(from)
                        .addSort("time",SortOrder.DESC)
                        .setQuery(queryBuilder).execute().actionGet();

        List<CrashBean> list = new ArrayList<>();


        Map<String, Object> result = new HashMap<>();

        result.put("total",response.getHits().getTotalHits());

        for (SearchHit searchHit : response.getHits().getHits()) {

            logger.debug("{}", searchHit.getSourceAsString());

            CrashBean crashBean = new CrashBean();
            Map map = searchHit.getSource();
            /**
             * 日志类型 当前日志的类型名称，取值：transaction
             */
            if (map.containsKey("log_type")) {
                Object object = map.get("log_type");
                if (object != null) {
                    crashBean.setLog_type((String) object);
                }
            }

            /**
             * 日志结构版本 日志结构的版本号
             */
            if (map.containsKey("log_ver")) {
                Object object = map.get("log_ver");
                if (object != null) {
                    crashBean.setLog_ver((String) object);
                }
            }

            /**
             * 应用名称 当前执行的系统名称，或者渠道名称
             */
            if (map.containsKey("system")) {
                Object object = map.get("system");
                if (object != null) {
                    crashBean.setSystem((String) object);
                }
            }


            /**
             * 系统版本号  当前应用的版本号
             */
            if (map.containsKey("ver")) {
                Object object = map.get("ver");
                if (object != null) {
                    crashBean.setVer((String) object);
                }
            }



            /**
             * 关键时间 当前节点的执行时间
             */
            if (map.containsKey("time")) {
                Object object = map.get("time");
                if (object != null) {
                    Date date = DateTime.parse((String) object).toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00"))).toDate();
                    crashBean.setTime(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS"));
                }
            }

            /**
             *
             */
            if (map.containsKey("techType")) {
                Object object = map.get("techType");
                if (object != null) {
                    crashBean.setTechType((String) object);
                }
            }

            if (map.containsKey("deviceType")) {
                Object object = map.get("deviceType");
                if (object != null) {
                    crashBean.setDeviceType((String) object);
                }
            }

            if (map.containsKey("resolution")) {
                Object object = map.get("resolution");
                if (object != null) {
                    crashBean.setResolution((String) object);
                }
            }

            if (map.containsKey("osVer")) {
                Object object = map.get("osVer");
                if (object != null) {
                    crashBean.setOsVer((String) object);
                }
            }

            if (map.containsKey("appID")) {
                Object object = map.get("appID");
                if (object != null) {
                    crashBean.setAppID((String) object);
                }
            }

            if (map.containsKey("network")) {
                Object object = map.get("network");
                if (object != null) {
                    crashBean.setNetwork((String) object);
                }
            }

            if (map.containsKey("module")) {
                Object object = map.get("module");
                if (object != null) {
                    crashBean.setModule((String) object);
                }
            }



            /**
             * 返回信息 当前节点执行后的返回的信息
             */
            if (map.containsKey("module_info")) {
                Object object = map.get("module_info");
                if (object != null) {
                    String info = (String) object;
                    crashBean.setMessage(info.replaceFirst("\u0003","").replaceAll("\u0003","<br />"));
                }
            }

            list.add(crashBean);
        }

        result.put("list",list);

        return result;
    }


    /**
     * 获取奔溃总数统计信息
     * @param
     * @return
     */
    public List<DateHistogramBean> getCrashDateHistogram(DateRange dateRange,String techType) {
        //封装结果集
        List<DateHistogramBean> dataList = new ArrayList<>();
        //组装分组
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        //定义分组的日期字段
        dateAgg.field("time");
        dateAgg.timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00")));
        dateAgg.interval(dateRange.getInterval());
        dateAgg.format(dateRange.getFormat());

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(dateRange.getStart().getTime())
                        .to(dateRange.getEnd().getTime())
        );

        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("techType", techType));

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getAppcrash())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(dateAgg)
                       // .setSize(0)
                        .setQuery(queryBuilder).execute().actionGet();

        Aggregations agg = response.getAggregations();
        if (agg != null) {
            InternalDateHistogram internalDateHistogram = agg.get("dateAgg");
            for (Histogram.Bucket bucket : internalDateHistogram.getBuckets()) {
                DateHistogramBean dateHistogramBean = new DateHistogramBean();
                dateHistogramBean.setKey(bucket.getKeyAsString());
                logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());
                dateHistogramBean.setCount(bucket.getDocCount());
                dataList.add(dateHistogramBean);
            }
        }

        return dataList;
    }


    /**
     * 获取奔溃总数统计信息
     * @param
     * @return
     */
    public List<Map<String,Object>> getCrashDateHistogramByType(DateRange dateRange,String techType,String type) {

        //组装分组
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        //定义分组的日期字段
        dateAgg.field("time");
        dateAgg.timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00")));
        dateAgg.interval(dateRange.getInterval());
        dateAgg.format(dateRange.getFormat());

        //根据 环境id分组 统计
        TermsAggregationBuilder termsAggregationBuilder =
                AggregationBuilders
                        .terms("group")
                        .field(type + ".keyword")
                        .size(5)
                        .order(Terms.Order.count(false))
                        .subAggregation(dateAgg);


        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(dateRange.getStart().getTime())
                        .to(dateRange.getEnd().getTime())
        );

        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("techType", techType));

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getAppcrash())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(termsAggregationBuilder)
                        .setSize(0)
                        .setQuery(queryBuilder).execute().actionGet();

        Aggregations agg = response.getAggregations();

        List<Map<String,Object>> result = new ArrayList<>();


        if (agg != null) {
            Terms terms = agg.get("group");
            if(terms != null){
                for (Terms.Bucket termsBucket : terms.getBuckets()){
                    Aggregations aggregations = termsBucket.getAggregations();
                    if(aggregations != null){
                        InternalDateHistogram internalDateHistogram = aggregations.get("dateAgg");
                        //封装结果集
                        List<DateHistogramBean> dataList = new ArrayList<>();
                        for (Histogram.Bucket bucket : internalDateHistogram.getBuckets()) {
                            DateHistogramBean dateHistogramBean = new DateHistogramBean();
                            dateHistogramBean.setKey(bucket.getKeyAsString());
                            logger.debug("{}-{}", bucket.getKey(), bucket.getDocCount());
                            dateHistogramBean.setCount(bucket.getDocCount());
                            dataList.add(dateHistogramBean);
                        }
                        Map map = new HashMap();
                        map.put(termsBucket.getKeyAsString(),dataList);
                        result.add(map);
                    }
                }
            }

        }

        return result;
    }


    /**
     * 获取奔溃应用版本列表
     * @param
     * @return
     */
    public List<String> getCrashAppVers(String system) {//DateRange dateRange

        //根据 环境id分组 统计
        TermsAggregationBuilder termsAggregationBuilder =
                AggregationBuilders
                        .terms("group")
                        .field("ver.keyword")
                        .size(50);

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("techType",system));

        //时间范围 必要筛选条件
//        queryBuilder = queryBuilder.must(
//                QueryBuilders.rangeQuery("date")
//                        .from(dateRange.getStart().getTime())
//                        .to(dateRange.getEnd().getTime())
//        );

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getAppcrash())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(termsAggregationBuilder)
                        .setSize(0)
                        .setQuery(queryBuilder).execute().actionGet();

        Aggregations agg = response.getAggregations();

        List<String> result = new ArrayList<>();

        if (agg != null) {
            Terms terms = agg.get("group");
            if(terms != null){
                for (Terms.Bucket termsBucket : terms.getBuckets()){
                    result.add(termsBucket.getKeyAsString());
                }
            }

        }


        Collections.sort(result);

        return result;
    }



    /**
     * @param dateRange
     * @param sourceApp
     * @param targetApp
     * @return count 流量 error 异常数
     */
    public Map<String, Long> getChainCount(DateRange dateRange, String sourceApp, String targetApp) {


        long startTime = dateRange.getStart().getTime();
        long endTime = dateRange.getEnd().getTime();
        //时间间隔 毫秒数
        long interval = (endTime - startTime) / 1000;

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(startTime)
                        .to(endTime)
        );

        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("last_system", targetApp));
        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("system", sourceApp));

        ValueCountAggregationBuilder valueCountAggregationBuilder
                = AggregationBuilders.count("count").field("interval");


        //status =2  调用远程方法产生异常
        FilterAggregationBuilder filterAggregationBuilder =
                AggregationBuilders.filter("error",
                        QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("status", "2")));

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getChainType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(valueCountAggregationBuilder)
                        .addAggregation(filterAggregationBuilder)
                        .setQuery(queryBuilder).addSort("time", SortOrder.ASC).execute().actionGet();

        Aggregations agg = response.getAggregations();

        Map<String, Long> map = new HashMap<>();
        map.put("count", 0l);
        map.put("error", 0l);
        if (agg != null) {
            ValueCount valueCount = agg.get("count");
            map.put("count", valueCount.getValue());
            Filter filter = agg.get("error");
            map.put("error", filter.getDocCount());
            return map;

        }

        return map;

    }

    /**
     * 应用SDK信息
     *
     * @param id
     * @param json
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void post(String id, String json) throws ExecutionException, InterruptedException {
        IndexRequest indexRequest = new IndexRequest(elasticConfig.getXwIndex(), elasticConfig.getEsType(), id).source(json);
        UpdateRequest updateRequest = new UpdateRequest(elasticConfig.getXwIndex(), elasticConfig.getEsType(), id).doc(json).upsert(indexRequest);
        transportClient.update(updateRequest).get();
    }

    /**
     * 跟新版本时间
     *
     * @param updateDate
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void postDate(String updateDate) throws ExecutionException, InterruptedException {
        IndexRequest indexRequest1 = new IndexRequest(elasticConfig.getXwIndex(), elasticConfig.getEsType(), elasticConfig.getEsId()).source(updateDate);
        UpdateRequest updateRequestDate = new UpdateRequest(elasticConfig.getXwIndex(), elasticConfig.getEsType(), elasticConfig.getEsId()).doc(updateDate).upsert(indexRequest1);
        transportClient.update(updateRequestDate).get();
    }


    public List<ChainBean> getChainAggregations(DateRange dateRange, String sourceAppCode, String targetAppCode) {
        List<ChainBean> list = new ArrayList<ChainBean>();

        //error 总数统计 日志包含 result_info 、 result_type ：分组子统计
        FilterAggregationBuilder filterAggregationBuilder =
                AggregationBuilders.filter("error",
                        QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("status", "0")));

        //统计时长 50线 95线 99线 ：分组子统计
        double[] p = new double[]{50, 95.0, 99.0};
        PercentilesAggregationBuilder percentiles =
                AggregationBuilders.percentiles("percentiles")
                        .field("interval")
                        .method(PercentilesMethod.HDR)
                        .numberOfSignificantValueDigits(5)
                        .percentiles(p);

        //统计时长 最大值 最小值 平均值 记录总数 ：分组子统计
        StatsAggregationBuilder statsAggregationBuilder =
                AggregationBuilders.stats("stats").field("interval");

        //根据 环境id分组 统计
        TermsAggregationBuilder termsAggregationBuilder =
                AggregationBuilders
                        .terms("last_node_code")
                        .field("last_node_code.keyword")
                        .size(1000)
                        .subAggregation(percentiles)
                        .subAggregation(statsAggregationBuilder)
                        .subAggregation(filterAggregationBuilder);

        TermsAggregationBuilder termsAggregationBuilder1 =
                AggregationBuilders
                        .terms("node_code")
                        .field("node_code.keyword")
                        .size(1000)
                        .subAggregation(termsAggregationBuilder);


        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        long startTime = dateRange.getStart().getTime();
        long endTime = dateRange.getEnd().getTime();
        //时间间隔 毫秒数
        long interval = (endTime - startTime) / 1000;

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(startTime)
                        .to(endTime)
        );

        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("system", sourceAppCode));

        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("last_system", targetAppCode));


        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getChainType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(termsAggregationBuilder1)
                        .setQuery(queryBuilder)
                        .addSort("interval", SortOrder.ASC).execute().actionGet();

        Aggregations aggregations1 = response.getAggregations();
        if (aggregations1 != null) {
            Terms terms = aggregations1.get("node_code");
            List<Terms.Bucket> buckets0 = terms.getBuckets();
            for (Terms.Bucket bucket : buckets0) {

                StringTerms stringTerms = (StringTerms) bucket.getAggregations().asMap().get("last_node_code");

                List<Terms.Bucket> buckets = stringTerms.getBuckets();
                for (Terms.Bucket bucket1 : buckets) {
                    ChainBean chainBean = new ChainBean();
                    logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());
                    chainBean.setSourceAppCode(sourceAppCode);
                    chainBean.setSourceMethod(bucket1.getKeyAsString());
                    chainBean.setTargetAppCode(targetAppCode);
                    chainBean.setTargetMethod(bucket.getKeyAsString());

                    Aggregations aggregations = bucket1.getAggregations();
                    if (aggregations != null) {
                        Percentiles agg = aggregations.get("percentiles");

                        for (Percentile entry : agg) {
                            double percent = entry.getPercent();    // Percent
                            double value = entry.getValue();        // Value
                            if (BigDecimal.valueOf(percent).compareTo(BigDecimal.valueOf(50)) == 0) {
                                chainBean.setLine50(convert(value));
                            } else if (BigDecimal.valueOf(percent).compareTo(BigDecimal.valueOf(95)) == 0) {
                                chainBean.setLine95(convert(value));
                            } else if (BigDecimal.valueOf(percent).compareTo(BigDecimal.valueOf(99)) == 0) {
                                chainBean.setLine99(convert(value));
                            }
                            logger.debug("percent [{}], value [{}]", percent, convert(value));
                        }

                        //Min min = bucket.getAggregations().get("min");

                        //logger.debug("min:{}", convert(min.getValue()));

                        Stats stats = aggregations.get("stats");
                        double min = stats.getMin();
                        double max = stats.getMax();
                        double avg = stats.getAvg();
                        double sum = stats.getSum();
                        long count = stats.getCount();
                        chainBean.setMin(convert(min));
                        chainBean.setMax(convert(max));
                        chainBean.setAvg(convert(avg));
                        chainBean.setCount(count);

                        logger.debug("min:{},max:{},avg:{},sum:{},count:{}", convert(min), convert(max), convert(avg), convert(sum), (count));

                        Filter filter = aggregations.get("error");
                        logger.debug("error:{}", filter.getDocCount());
                        chainBean.setErrorCount(filter.getDocCount());
                        chainBean.setErrorRate(filter.getDocCount() / count);

                        chainBean.setQbs(Double.valueOf(new DecimalFormat("0.00").format(count / (double) interval)));

                        logger.debug("{}", chainBean);
                        list.add(chainBean);

                    }
                }


            }
        }

        return list;

    }


    public List<Map<String, String>> getChains(DateRange dateRange, String sourceAppCode, String targetAppCode,boolean error) {

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        long startTime = dateRange.getStart().getTime();
        long endTime = dateRange.getEnd().getTime();
        //时间间隔 毫秒数
        long interval = (endTime - startTime) / 1000;

        if(error){
            queryBuilder.must(QueryBuilders.matchQuery("status", "2"));
        }else{
            queryBuilder.mustNot(QueryBuilders.matchQuery("status", "1"));
            queryBuilder.mustNot(QueryBuilders.matchQuery("status", "2"));
        }


        //时间范围 必要筛选条件
        queryBuilder.must(QueryBuilders.rangeQuery("time").from(startTime).to(endTime));

        queryBuilder.must(QueryBuilders.matchQuery("system",sourceAppCode ).operator(Operator.AND));

        queryBuilder.must(QueryBuilders.matchQuery("last_system",targetAppCode ).operator(Operator.AND));


        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getChainType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(queryBuilder)
                        .setSize(750)
                        .addSort(SortBuilders.scriptSort(new Script("Math.random()"), ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.ASC))
                        .execute().actionGet();

        List<Map<String, String>> result = new ArrayList<>();

        response.getHits().forEach(hit -> {

            Map map = new HashMap();

            Date date = DateTime.parse((String) hit.getSource().get("time")).toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00"))).toDate();

            map.put("mills", date.getTime());
            map.put("time", DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS"));
            map.put("interval", convert((double) hit.getSource().get("interval")));
            map.put("error", hit.getSource().get("result_type"));
            map.put("id", hit.getSource().get("service_ID"));
            result.add(map);


        });

        Collections.sort(result, new TimeComparator()); // 根据价格排序


        return result;

    }


    public List<Map> getChainsAggByTime(DateRange dateRange, String sourceAppCode, String targetAppCode) {

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        long startTime = dateRange.getStart().getTime();
        long endTime = dateRange.getEnd().getTime();
        //时间间隔 毫秒数
        long interval = (endTime - startTime) / 1000;

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery("time").from(startTime).to(endTime));

        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("system",sourceAppCode ).operator(Operator.AND));

        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("last_system",targetAppCode ).operator(Operator.AND));


        logger.debug(queryBuilder.toString());

        FilterAggregationBuilder filterAggregationBuilderi1 =
                AggregationBuilders.filter("i1",
                        QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("interval").from(0l).to(1000000000l)));

        FilterAggregationBuilder filterAggregationBuilderi2 =
                AggregationBuilders.filter("i2",
                        QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("interval").from(1000000001l).to(2000000000l)));

        FilterAggregationBuilder filterAggregationBuilderi3 =
                AggregationBuilders.filter("i3",
                        QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("interval").from(2000000001l).to(3000000000l)));

        FilterAggregationBuilder filterAggregationBuilderi4 =
                AggregationBuilders.filter("i4",
                        QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("interval").from(3000000001l).to(4000000000l)));

        FilterAggregationBuilder filterAggregationBuilderi5 =
                AggregationBuilders.filter("i5",
                        QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("interval").from(4000000001l)));


        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getChainType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(queryBuilder)
                        .addAggregation(filterAggregationBuilderi1)
                        .addAggregation(filterAggregationBuilderi2)
                        .addAggregation(filterAggregationBuilderi3)
                        .addAggregation(filterAggregationBuilderi4)
                        .addAggregation(filterAggregationBuilderi5)
                        .execute().actionGet();

        List<Map> list = new ArrayList<>();

        Aggregations aggregations = response.getAggregations();
        if (aggregations != null) {
            Filter filter = aggregations.get("i1");
            Map map = new HashMap();
            map.put("x", "1s");
            map.put("y", filter.getDocCount());
            list.add(map);
            filter = aggregations.get("i2");
            map = new HashMap();
            map.put("x", "2s");
            map.put("y", filter.getDocCount());
            list.add(map);

            filter = aggregations.get("i3");
            map = new HashMap();
            map.put("x", "3s");
            map.put("y", filter.getDocCount());
            list.add(map);

            filter = aggregations.get("i4");
            map = new HashMap();
            map.put("x", "4s");
            map.put("y", filter.getDocCount());
            list.add(map);

            filter = aggregations.get("i5");
            map = new HashMap();
            map.put("x", "5s+");
            map.put("y", filter.getDocCount());
            list.add(map);


        }


        return list;

    }


    // 自定义比较器：按书的价格排序
    class TimeComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Map p1 = (Map) object1; // 强制转换
            Map p2 = (Map) object2;
            return p1.get("time").toString().compareTo(p2.get("time").toString());
        }
    }


    /**
     * 查询app错误数
     * @param appCode
     * @return
     */
    public AppErrorBean getAppError(String appCode) {

        List<AppErrorBean> appErrorBeans = new ArrayList<>();

        //根据 环境id分组 统计
//        TermsAggregationBuilder termsAggregationBuilder =
//                AggregationBuilders
//                        .terms("system")
//                        .field("system.keyword")
//                        .size(10);

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .mustNot(QueryBuilders.matchQuery("status", "0").operator(Operator.AND));

        Calendar calendar = Calendar.getInstance();
        Date end = calendar.getTime();

        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date start = calendar.getTime();
        //时间范围 必要筛选条件
        queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(start.getTime())
                        .to(end.getTime())
        ).must(QueryBuilders.matchQuery("system", appCode).operator(Operator.AND));



        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getTransactionType(), elasticConfig.getMethodType(), elasticConfig.getChainType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        //.addAggregation(termsAggregationBuilder)
                        .setSize(0)
                        .setQuery(queryBuilder).execute().actionGet();

         AppErrorBean appErrorBean = new AppErrorBean();
                appErrorBean.setAppCode(appCode);
                appErrorBean.setCount(response.getHits().getTotalHits());
                appErrorBeans.add(appErrorBean);

//        Aggregations aggregations = response.getAggregations();
//        if (aggregations != null) {
//            Terms terms = aggregations.get("system");
//            List<Terms.Bucket> buckets = terms.getBuckets();
//            for (Terms.Bucket bucket : buckets) {
//                AppErrorBean appErrorBean = new AppErrorBean();
//                appErrorBean.setAppCode(bucket.getKeyAsString());
//                appErrorBean.setCount(bucket.getDocCount());
//                appErrorBeans.add(appErrorBean);
//            }
//
//
//        }

        return appErrorBean;


    }


    /**
     * 查询近一小时 节点错误数 top10
     * @param appCode
     * @return
     */
    public List<NodeErrorBean> getNodeError(String appCode) {

        List<NodeErrorBean> nodeErrorBeans = new ArrayList<>();

        //根据 环境id分组 统计
        TermsAggregationBuilder termsAggregationBuilder =
                AggregationBuilders
                        .terms("node_code")
                        .field("node_code.keyword")
                        .size(5)
                        .order(Terms.Order.count(false));

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        Calendar calendar = Calendar.getInstance();
        Date end = calendar.getTime();

        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date start = calendar.getTime();
        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(start.getTime())
                        .to(end.getTime())
        );


        queryBuilder.must(QueryBuilders.matchQuery("system", appCode).operator(Operator.AND));

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getTransactionType(), elasticConfig.getMethodType(), elasticConfig.getChainType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(termsAggregationBuilder)
                        .setQuery(queryBuilder).execute().actionGet();

        Aggregations aggregations = response.getAggregations();
        if (aggregations != null) {
            Terms terms = aggregations.get("node_code");
            List<Terms.Bucket> buckets = terms.getBuckets();
            for (Terms.Bucket bucket : buckets) {
                NodeErrorBean nodeErrorBean = new NodeErrorBean();
                nodeErrorBean.setNodeCode(bucket.getKeyAsString());
                nodeErrorBean.setCount(bucket.getDocCount());
                nodeErrorBeans.add(nodeErrorBean);
            }
        }

        return nodeErrorBeans;


    }


    /**
     * 查询近一小时主机错误数 top10
     * @param appCode
     * @return
     */
    public List<NodeErrorBean> getHostError(String appCode) {

        List<NodeErrorBean> nodeErrorBeans = new ArrayList<>();

        //根据 环境id分组 统计
        TermsAggregationBuilder termsAggregationBuilder =
                AggregationBuilders
                        .terms("server_ip")
                        .field("server_ip.keyword")
                        .size(10)
                        .order(Terms.Order.count(false))
                ;

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("status", "0"));

        Calendar calendar = Calendar.getInstance();
        Date end = calendar.getTime();

        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date start = calendar.getTime();
        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(start.getTime())
                        .to(end.getTime())
        );

        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("system", appCode).operator(Operator.AND));

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(elasticConfig.getTransactionType(), elasticConfig.getMethodType(), elasticConfig.getChainType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(termsAggregationBuilder)
                        .setQuery(queryBuilder).execute().actionGet();

        Aggregations aggregations = response.getAggregations();
        if (aggregations != null) {
            Terms terms = aggregations.get("server_ip");
            List<Terms.Bucket> buckets = terms.getBuckets();
            for (Terms.Bucket bucket : buckets) {
                NodeErrorBean nodeErrorBean = new NodeErrorBean();
                nodeErrorBean.setNodeCode(bucket.getKeyAsString());
                nodeErrorBean.setCount(bucket.getDocCount());
                nodeErrorBeans.add(nodeErrorBean);
            }


        }

        return nodeErrorBeans;


    }


    public List<ErrorsBean> getErrorsAgg(
            DateRange dateRange,
            String appCode,
            String host,
            String port,
            String key) {


        List<ErrorsBean> list = new ArrayList<>();

        //error 总数统计 日志包含 result_info 、 result_type ：分组子统计
        FilterAggregationBuilder filterAggregationBuilder =
                AggregationBuilders.filter("error",
                        QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("status", "0")));

        //统计时长 50线 95线 99线 ：分组子统计
//        double[] p = new double[]{50, 95.0, 99.0};
//        PercentilesAggregationBuilder percentiles =
//                AggregationBuilders.percentiles("percentiles")
//                        .field("interval")
//                        .method(PercentilesMethod.HDR)
//                        .numberOfSignificantValueDigits(5)
//                        .percentiles(p);

        //统计时长 最大值 最小值 平均值 记录总数 ：分组子统计
        StatsAggregationBuilder statsAggregationBuilder =
                AggregationBuilders.stats("stats").field("interval");

        //根据 环境id分组 统计
        TermsAggregationBuilder termsAggregationBuilder =
                AggregationBuilders
                        .terms("result_type")
                        .field("result_type.keyword")
                        .size(1000)
//                        .subAggregation(percentiles)
                        .subAggregation(statsAggregationBuilder)
                        .subAggregation(filterAggregationBuilder).order(Terms.Order.count(false));


        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        long startTime = dateRange.getStart().getTime();
        long endTime = dateRange.getEnd().getTime();
        //时间间隔 毫秒数
        long interval = (endTime - startTime) / 1000;

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(startTime)
                        .to(endTime)
        );

        if (!StringUtils.isEmpty(key)) {
            queryBuilder = queryBuilder.must(QueryBuilders.regexpQuery("result_type",".*" + key + ".*"));
        }

        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("system", appCode));
        //主机 可选条件
        if (!StringUtils.isEmpty(host)) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_ip", host));
            //端口 可选条件
            if (!StringUtils.isEmpty(port)) {
                queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_port", port));
            }
        }


        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(
                                elasticConfig.getTransactionType(),
                                elasticConfig.getMethodType(),
                                elasticConfig.getChainType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(termsAggregationBuilder)
                        .setQuery(queryBuilder)
                        .execute().actionGet();

        Aggregations aggregations = response.getAggregations();
        if (aggregations != null) {
            Terms terms = aggregations.get("result_type");
            List<Terms.Bucket> buckets0 = terms.getBuckets();
            for (Terms.Bucket bucket : buckets0) {

                ErrorsBean errorsBean = new ErrorsBean();

                errorsBean.setErrorType(bucket.getKeyAsString());


                Stats stats = bucket.getAggregations().get("stats");

                double min = stats.getMin();
                double max = stats.getMax();
                double avg = stats.getAvg();
                double sum = stats.getSum();
                long count = stats.getCount();

                errorsBean.setCount(count);

//                chainBean.setMin(convert(min));
//                chainBean.setMax(convert(max));
//                chainBean.setAvg(convert(avg));
//                chainBean.setCount(count);

                logger.debug("min:{},max:{},avg:{},sum:{},count:{}", convert(min), convert(max), convert(avg), convert(sum), (count));

                Filter filter = bucket.getAggregations().get("error");

                logger.debug("error:{}", filter.getDocCount());
//                chainBean.setErrorCount(filter.getDocCount());
//                chainBean.setErrorRate(filter.getDocCount() / count);
//
//                chainBean.setQbs(Double.valueOf(new DecimalFormat("0.00").format(count / (double) interval)));
//
//                logger.debug("{}", chainBean);
                list.add(errorsBean);

            }
        }

        return list;


    }


    public List<DateHistogramBean> getErrorsDateAgg(
            DateRange dateRange, String appCode, String host, String port, String resultType, String key) {

        //封装结果集
        List<DateHistogramBean> dataList = new ArrayList<>();
        //组装分组
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");

        //定义分组的日期字段
        dateAgg.field("time");
        dateAgg.timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00")));
        dateAgg.interval(dateRange.getInterval());
        dateAgg.format(dateRange.getFormat());



        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        long startTime = dateRange.getStart() .getTime();
        long endTime = dateRange.getEnd().getTime();
        //时间间隔 毫秒数
        long interval = (endTime - startTime) / 1000;

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(startTime)
                        .to(endTime)
        );

        queryBuilder.must(QueryBuilders.matchQuery("result_type", resultType));


        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("system", appCode));
        //主机 可选条件
        if (!StringUtils.isEmpty(host)) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_ip", host));
            //端口 可选条件
            if (!StringUtils.isEmpty(port)) {
                queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_port", port));
            }
        }

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(
                                elasticConfig.getTransactionType(),
                                elasticConfig.getMethodType(),
                                elasticConfig.getChainType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .addAggregation(dateAgg)
                        .setQuery(queryBuilder).execute().actionGet();

        Aggregations agg = response.getAggregations();
        if (agg != null) {
            InternalDateHistogram internalDateHistogram = agg.get("dateAgg");
            for (Histogram.Bucket bucket : internalDateHistogram.getBuckets()) {
                DateHistogramBean dateHistogramBean = new DateHistogramBean();
                dateHistogramBean.setKey(bucket.getKeyAsString());
                logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());
                dateHistogramBean.setCount(bucket.getDocCount());
                dateHistogramBean.setErrorCount(bucket.getDocCount());
                dataList.add(dateHistogramBean);
            }
        }

        return dataList;


    }



    public Map<String, Object> getErrorDetails(
            Date start,Date end, String appCode, String host, String port, String resultType, String key, int from, int size) {

        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        long startTime = start .getTime();
        long endTime = end.getTime();

        //时间范围 必要筛选条件
        queryBuilder = queryBuilder.must(
                QueryBuilders.rangeQuery("time")
                        .from(startTime)
                        .to(endTime)
        );

        queryBuilder.must(QueryBuilders.matchQuery("result_type", resultType));


        queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("system", appCode));
        //主机 可选条件
        if (!StringUtils.isEmpty(host)) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_ip", host));
            //端口 可选条件
            if (!StringUtils.isEmpty(port)) {
                queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_port", port));
            }
        }

        logger.debug(queryBuilder.toString());

        SearchResponse response =
                transportClient.prepareSearch(elasticConfig.getApplicationIndex())
                        .setTypes(
                                elasticConfig.getTransactionType(),
                                elasticConfig.getMethodType(),
                                elasticConfig.getChainType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setSize(size).setFrom(from)
                        .setQuery(queryBuilder).execute().actionGet();

        Map<String, Object> result = new HashMap<>();
        result.put("total",response.getHits().getTotalHits());

        List<LogBean> logBeans = new ArrayList<>();

        for (SearchHit searchHit : response.getHits().getHits()) {

            logger.debug("{}", searchHit.getSourceAsString());

            LogBean logBean = new LogBean();
            Map map = searchHit.getSource();
            /**
             * 日志类型 当前日志的类型名称，取值：transaction
             */
            if (map.containsKey("log_type")) {
                Object object = map.get("log_type");
                if (object != null) {
                    logBean.setLog_type((String) object);
                }
            }

            /**
             * 日志结构版本 日志结构的版本号
             */
            if (map.containsKey("log_ver")) {
                Object object = map.get("log_ver");
                if (object != null) {
                    logBean.setLog_ver((String) object);
                }
            }

            /**
             * 应用名称 当前执行的系统名称，或者渠道名称
             */
            if (map.containsKey("system")) {
                Object object = map.get("system");
                if (object != null) {
                    logBean.setSystem((String) object);
                }
            }

            if (map.containsKey("last_system")) {
                Object object = map.get("last_system");
                if (object != null) {
                    logBean.setLast_system((String) object);
                }
            }


            /**
             * 系统版本号  当前应用的版本号
             */
            if (map.containsKey("ver")) {
                Object object = map.get("ver");
                if (object != null) {
                    logBean.setVer((String) object);
                }
            }

            /**
             * 微环境ID 当前事务所属微环境，可以作为识别某类事务的名称，比如某个活动的名称等，微环境可以包含一组不同的事务，缺省值为：default
             */
            if (map.containsKey("envir_ID")) {
                Object object = map.get("envir_ID");
                if (object != null) {
                    logBean.setEnvir_ID((String) object);
                }
            }


            /**
             * 关键时间 当前节点的执行时间
             */
            if (map.containsKey("time")) {
                Object object = map.get("time");
                if (object != null) {

                    Date date = DateTime.parse((String) object).toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00"))).toDate();

                    logBean.setTime(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS"));
                }
            }


            /**
             * 执行耗时 当前节点执行的总耗时纳秒数
             */
            if (map.containsKey("interval")) {
                Object object = map.get("interval");
                if (object != null) {
                    logBean.setInterval(convert((double) object));
                }
            }


            /**
             * 状态 当前节点执行后的返回码
             */
            if (map.containsKey("status")) {
                Object object = map.get("status");
                if (object != null) {
                    logBean.setStatus((String) object);
                }
            }


            /**
             * 返回信息类型 返回信息的类型，比如各种具体的异常类型等
             */

            if (map.containsKey("result_type")) {
                Object object = map.get("result_type");
                if (object != null) {
                    logBean.setResult_type((String) object);
                }
            }


            /**
             * 返回信息 当前节点执行后的返回的信息
             */
            if (map.containsKey("result_info")) {
                Object object = map.get("result_info");
                if (object != null) {
                    String info = (String) object;
                    logBean.setResult_info(info.replaceFirst("\u0003","").replaceAll("\u0003","<br />"));
                }
            }

            /**
             * 服务编码 当前一次针对服务的请求的ID
             */
            if (map.containsKey("service_ID")) {
                Object object = map.get("service_ID");
                if (object != null) {
                    logBean.setService_ID((String) object);
                }
            }


            /**
             * 当前节点ID 当前事务中，当前节点的ID
             */
            if (map.containsKey("node_ID")) {
                Object object = map.get("node_ID");
                if (object != null) {
                    logBean.setNode_ID((String) object);
                }
            }


            /**
             * 节点名称 当前节点的名称，也可以是方法名称等
             */
            if (map.containsKey("node_code")) {
                Object object = map.get("node_code");
                if (object != null) {
                    logBean.setNode_code((String) object);
                }
            }


            /**
             * 上级节点ID 当前事务中，上级节点的ID，若当前结点是根节点，则此为空
             */
            if (map.containsKey("last_node_ID")) {
                Object object = map.get("last_node_ID");
                if (object != null) {
                    logBean.setLast_node_ID((String) object);
                }
            }


            /**
             * 上级节点名称 上级节点的名称，也可以是方法名称等
             */
            if (map.containsKey("last_node_code")) {
                Object object = map.get("last_node_code");
                if (object != null) {
                    logBean.setLast_node_code((String) object);
                }
            }


            /**
             * 主机IP 当前节点执行的服务器地址
             */
            if (map.containsKey("server_ip")) {
                Object object = map.get("server_ip");
                if (object != null) {
                    logBean.setServer_ip((String) object);
                }
            }

            /**
             * 执行端口 当前节点执行的服务器端口
             */
            if (map.containsKey("server_port")) {
                Object object = map.get("server_port");
                if (object != null) {
                    logBean.setServer_port((String) object);
                }
            }

            /**
             * 调用模式
             */
            if (map.containsKey("mode")) {
                Object object = map.get("mode");
                if (object != null) {
                    logBean.setMode((String) object);
                }
            }

            /**
             * 客户端类型 Android|IOS|Server|Method
             */
            if (map.containsKey("client_type")) {
                Object object = map.get("client_type");
                if (object != null) {
                    logBean.setClient_type((String) object);
                }
            }

            /**
             * 客户端IP 客户端地址
             */
            if (map.containsKey("client_ip")) {
                Object object = map.get("client_ip");
                if (object != null) {
                    logBean.setClient_ip((String) object);
                }
            }

            /**
             * 客户端端口
             */
            if (map.containsKey("client_port")) {
                Object object = map.get("client_port");
                if (object != null) {
                    logBean.setClient_port((String) object);
                }
            }

            /**
             * 终端信息
             */

            if (map.containsKey("client_info")) {
                Object object = map.get("client_info");
                if (object != null) {
                    logBean.setClient_info((String) object);
                }
            }

            /**
             * 自定义属性 针对业务可自定义的属性集合，用字符”&“进行分割，属性是”key=value“结构
             */
            if (map.containsKey("custm_property")) {
                Object object = map.get("custm_property");
                if (object != null) {
                    logBean.setCustm_property((String) object);
                }
            }

            /**
             * 具体发起调用的方法节点ID
             */
            if (map.containsKey("invoke_node_ID")) {
                Object object = map.get("invoke_node_ID");
                if (object != null) {
                    logBean.setInvoke_node_ID((String) object);
                }
            }

            /**
             * 具体发起调用的方法名称
             */
            if (map.containsKey("invoke_node_code")) {
                Object object = map.get("invoke_node_code");
                if (object != null) {
                    logBean.setInvoke_node_code((String) object);
                }
            }

            logBeans.add(logBean);
        }

        result.put("list",logBeans);

        return result;


    }


}
