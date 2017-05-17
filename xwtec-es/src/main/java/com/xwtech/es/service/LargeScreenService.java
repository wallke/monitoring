package com.xwtech.es.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xwtech.es.ConstantsCode;
import com.xwtech.es.DateFormatUtil;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.model.RecentHostBean;
import com.xwtech.es.model.RecentWarnBean;

import io.netty.util.internal.StringUtil;

/**
 * Created by zl on 2017/2/24. 大屏数据获取接口
 */
@Service
public class LargeScreenService {

	private static Logger logger = LoggerFactory.getLogger(LargeScreenService.class);

	@Autowired
	TransportClient transportClient;

	@Autowired
	ElasticConfig elasticConfig;

	private static final Long timeGTM = 8 * 60 * 60 * 1000L;

	private static final Long minFormSS = 60 * 1000L;

	private static final String index_alert = "xwtec-alert-*";

	private static final String type_alert = "alert.stat";
	
	private static final DecimalFormat df = new DecimalFormat("0.00");

	/**
	 * 系统时间戳
	 * 
	 * @return
	 */
	public Long getDateTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 系统安全运行时间
	 */
	public int getSaleOperDay() {
		long noWaring = getLastWarningDay(4);// 获取最近4级以上预警最近发生时间
		long now = System.currentTimeMillis();// 得到当前的毫秒
		int day = (int) ((now - noWaring) / 1000 / 60 / 60);
		return day;
	}

	/**
	 * 根据预警等级获取 最后一次预警时间
	 * 
	 * @param level
	 * @return
	 */
	private Long getLastWarningDay(int level) {

		Long date = 1490339797016L;

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

		// 当前时间范围
		queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.alert_level).from(level));

		logger.debug(queryBuilder.toString());

		SearchResponse response = transportClient.prepareSearch(index_alert).setTypes(type_alert)
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH).setQuery(queryBuilder)
				.addSort(ConstantsCode.alert_time, SortOrder.DESC).execute().actionGet();

		if (response == null) {
			return date;
		}

		if (response.getHits() == null) {
			return date;
		}

		if (response.getHits().hits().length == 0) {
			return date;
		}

		SearchHit hit = response.getHits().hits()[0];

		Map<String, Object> obj = hit.getSource();

		if (obj.containsKey(ConstantsCode.alert_time)) {
			try {
				date =  DateFormatUtil.DateFormatGMT(String.valueOf(obj.get(ConstantsCode.alert_time)),DateFormatUtil.GTMFormatMilSSS);
			} catch (Exception e) {
				e.printStackTrace();
				return date;
			}
		} else {
			return date;
		}

		return date;
	}

	/**
	 * 无预警累计
	 */
	public int getNoWarningDay() {
		long noWaring = getLastWarningDay(2);// 获取最近2级以上预警最近发生时间
		long now = System.currentTimeMillis();// 得到当前的毫秒
		int day = (int) ((now - noWaring) / 1000 / 60 / 60 / 24);
		return day;
	}

	/**
	 * 服务总数
	 */
	public int getServiceCount() {
		int count = 0;
		try {

			// 根据node_code 分组
			TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(ConstantsCode.node_code)
					.field(ConstantsCode.node_code_keyword);

			// 设置查询条件
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 当天时间开始
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(sdf.parse(dateNow).getTime()).to(System.currentTimeMillis()));

			logger.debug(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getApplicationIndex())
					.setTypes(elasticConfig.getTransactionType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.addAggregation(termsAggregationBuilder).setQuery(queryBuilder).execute().actionGet();
			Aggregations aggregations = response.getAggregations();
			if (aggregations != null) {
				Terms terms = aggregations.get(ConstantsCode.node_code);
				count = terms.getBuckets().size();
			}

		} catch (Exception e) {
			logger.debug(e.getMessage());
			return count;
		}
		return count;
	}

	/**
	 * 错误率
	 * 
	 * @return
	 */
	public Double getErrorRate() {

		double errorRate = 0.00;

		try {
			// 设置查询条件
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 当天时间开始
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateNow = sdf.format(new Date());
			// 当前时间范围 查询总量
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(sdf.parse(dateNow).getTime()).to(System.currentTimeMillis()));

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getApplicationIndex())
					.setTypes(elasticConfig.getTransactionType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.setQuery(queryBuilder).execute().actionGet();
			Long total = response.getHits().getTotalHits();

			// 设置查询条件
			BoolQueryBuilder queryBuilderError = QueryBuilders.boolQuery();

			// 当前时间范围 查询错误的
			queryBuilderError = queryBuilderError.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(sdf.parse(dateNow).getTime()).to(System.currentTimeMillis()))
					.must(QueryBuilders.existsQuery("result_info"));

			logger.debug(queryBuilderError.toString());

			SearchResponse responseError = transportClient.prepareSearch(elasticConfig.getApplicationIndex())
					.setTypes(elasticConfig.getTransactionType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.setQuery(queryBuilderError).execute().actionGet();

			Long error = responseError.getHits().getTotalHits();

			if (total != 0) {
				errorRate = (double) error / total;
			}

		} catch (Exception e) {
			logger.debug(e.getMessage());
			return errorRate;
		}

		return errorRate;

	}

	/**
	 * 今日预警
	 * 
	 * @return
	 */
	public int getTodayWraingCount() {

		int total = 0;
		try {
			// 设置查询条件
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 当天时间开始
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.alert_time)
					.from(sdf.parse(dateNow).getTime()).to(System.currentTimeMillis()));

			logger.debug(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(index_alert).setTypes(type_alert)
					.setSearchType(SearchType.DFS_QUERY_AND_FETCH).setQuery(queryBuilder).execute().actionGet();

			total = (int) response.getHits().getTotalHits();

		} catch (Exception e) {
			e.printStackTrace();
			return total;
		}

		return total;
	}

	/**
	 * 近X日预警动态 
	 */
	public RecentWarnBean getRecentwarning(int day) {
		RecentWarnBean bean = new RecentWarnBean();
		bean.setDuration(day);

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		try {
			// 根据alert_level 分组
			TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(ConstantsCode.alert_level)
					.field(ConstantsCode.alert_level);

			// 设置查询条件
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 当天时间开始
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.alert_time)
					.from(sdf.parse(dateNow).getTime() - day * 24 * 60 * 60 * 1000L).to(System.currentTimeMillis()));

			logger.debug(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(index_alert)
					.setTypes(type_alert).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.addAggregation(termsAggregationBuilder)
					.setQuery(queryBuilder)
					.addSort(ConstantsCode.alert_level, SortOrder.DESC)
					.execute().actionGet();
			
			Aggregations aggregations = response.getAggregations();
			if (aggregations != null) {
				Terms terms = aggregations.get(ConstantsCode.alert_level);
				for (Terms.Bucket bucket : terms.getBuckets()) {
					logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());

					Map<String, Object> map = new HashMap<String, Object>();
					String name = "";
                    if("1".equals(String.valueOf(bucket.getKey()))){
                    	name = "一级预警";
                    }else  if("2".equals(String.valueOf(bucket.getKey()))){
                    	name = "二级预警";
                    }else  if("3".equals(String.valueOf(bucket.getKey()))){
                    	name = "三级预警";
                    }else  if("4".equals(String.valueOf(bucket.getKey()))){
                    	name = "四级预警";
                    }else  if("5".equals(String.valueOf(bucket.getKey()))){
                    	name = "五级预警";
                    }
					map.put(ConstantsCode.name, name);
					map.put(ConstantsCode.value, bucket.getDocCount());
					data.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		bean.setData(data);
		return bean;
	}

	/**
	 * 近X日预警类型
	 */
	public RecentWarnBean getTypewarning(int day) {
		RecentWarnBean bean = new RecentWarnBean();
		bean.setDuration(day);

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		
		try {
			// 根据target_template_keyword 分组
			TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(ConstantsCode.target_template)
					.field(ConstantsCode.target_template_keyword);

			// 设置查询条件
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 当天时间开始
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.alert_time)
					.from(sdf.parse(dateNow).getTime() - day * 24 * 60 * 60 * 1000L).to(System.currentTimeMillis()));

			logger.debug(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(index_alert)
					.setTypes(type_alert).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.addAggregation(termsAggregationBuilder)
					.setQuery(queryBuilder)
					.addSort(ConstantsCode.alert_level, SortOrder.DESC)
					.execute().actionGet();
			
			Aggregations aggregations = response.getAggregations();
			if (aggregations != null) {
				Terms terms = aggregations.get(ConstantsCode.target_template);
				for (Terms.Bucket bucket : terms.getBuckets()) {
					logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());

					Map<String, Object> map = new HashMap<String, Object>();
					String name = "";
                    if("ServiceMethod".equals(String.valueOf(bucket.getKey()))){
                    	name = "应用监控";
                    }else  if("InvokeChain".equals(String.valueOf(bucket.getKey()))){
                    	name = "调用连监控";
                    }else  if("Host".equals(String.valueOf(bucket.getKey()))){
                    	name = "主机监控";
                    }else  if("Redis".equals(String.valueOf(bucket.getKey()))){
                    	name = "Redis性能";
                    }else  if("Nginx".equals(String.valueOf(bucket.getKey()))){
                    	name = "Nginx性能";
                    }
					map.put(ConstantsCode.name, name);
					map.put(ConstantsCode.value, bucket.getDocCount());
					data.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		bean.setData(data);

		return bean;
	}

	/**
	 * 微环境请求次数 TOP5
	 */
	public List<Map<String, Object>> getMinenvironmentTop(int day) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {

			// 根据node_code 分组,并且取前5
			TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(ConstantsCode.envir_ID)
					.field(ConstantsCode.envir_ID_keyword).size(5);

			// 设置查询条件
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 当天时间开始
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(sdf.parse(dateNow).getTime() - day * 24 * 60 * 60 * 1000L).to(System.currentTimeMillis()));
			
			queryBuilder = queryBuilder.mustNot(QueryBuilders.matchQuery(ConstantsCode.envir_ID, "default"));

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getApplicationIndex())
					.setTypes(elasticConfig.getTransactionType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.addAggregation(termsAggregationBuilder).setQuery(queryBuilder).execute().actionGet();
			Aggregations aggregations = response.getAggregations();
			if (aggregations != null) {
				Terms terms = aggregations.get(ConstantsCode.envir_ID);
				for (Terms.Bucket bucket : terms.getBuckets()) {
					logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());

					Map<String, Object> map = new HashMap<String, Object>();
					map.put(ConstantsCode.name, String.valueOf(bucket.getKey()));
					map.put(ConstantsCode.value, bucket.getDocCount());
					list.add(map);
				}
			}

		} catch (Exception e) {
			logger.debug(e.getMessage());
			return null;
		}
		return list;

	}

	/**
	 * 接口请求时长 接口请求时长TOP5
	 * 
	 */
	public List<Map<String, Object>> getInterFaceTop(int day) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {

			// 根据node_code 分组,并且取前5
			TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(ConstantsCode.node_code)
					.field(ConstantsCode.node_code_keyword);

			// 根据时长sum
			AvgAggregationBuilder AvgAggregationBuilder = AggregationBuilders.avg(ConstantsCode.interval_sum)
					.field(ConstantsCode.interval);

			// 分组后时长SUM，并且根据sum倒序
			termsAggregationBuilder.subAggregation(AvgAggregationBuilder)
					.order(Order.aggregation(ConstantsCode.interval_sum, false));

			// 设置查询条件
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 当天时间开始
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(sdf.parse(dateNow).getTime() - day * 24 * 60 * 40 * 1000L).to(System.currentTimeMillis()));

			logger.debug(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getApplicationIndex())
					.setTypes(elasticConfig.getChainType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.addAggregation(termsAggregationBuilder).setQuery(queryBuilder).execute().actionGet();
			Aggregations aggregations = response.getAggregations();
			if (aggregations != null) {
				Terms terms = aggregations.get(ConstantsCode.node_code);
				for (Terms.Bucket bucket : terms.getBuckets()) {
					logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());

					Map<String, Object> map = new HashMap<String, Object>();
					map.put(ConstantsCode.name, String.valueOf(bucket.getKey()));
					// map.put(ConstantsCode.value, bucket.getDocCount());
					InternalAvg avg = bucket.getAggregations().get(ConstantsCode.interval_sum);
					// 次数变成时长，毫秒
					map.put(ConstantsCode.value, df.format((avg.getValue()/100000000)));
					list.add(map);
				}
			}

		} catch (Exception e) {
			logger.debug(e.getMessage());
			return null;
		}
		return list;

	}

	/**
	 * 近X天微环境请求TOP百分比
	 * 
	 */

	public RecentWarnBean getMinenvironmentTopRate(int day) {
		RecentWarnBean bean = new RecentWarnBean();
		bean.setDuration(day);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {

			// 根据node_code 分组,并且取前5
			TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(ConstantsCode.envir_ID)
					.field(ConstantsCode.envir_ID_keyword).size(5);

			// 设置查询条件
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 当天时间开始
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateNow = sdf.format(new Date());
			// day天的查询时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(sdf.parse(dateNow).getTime() - day * 24 * 60 * 60 * 1000L).to(System.currentTimeMillis()));
			
			queryBuilder = queryBuilder.mustNot(QueryBuilders.matchQuery(ConstantsCode.envir_ID, "default"));

			logger.debug(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getApplicationIndex())
					.setTypes(elasticConfig.getTransactionType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.addAggregation(termsAggregationBuilder).setQuery(queryBuilder).execute().actionGet();
//			Long totle = response.getHits().getTotalHits();
			Aggregations aggregations = response.getAggregations();
			if (aggregations != null) {
				Terms terms = aggregations.get(ConstantsCode.envir_ID);
				for (Terms.Bucket bucket : terms.getBuckets()) {
					logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());

					Map<String, Object> map = new HashMap<String, Object>();
					map.put(ConstantsCode.name, String.valueOf(bucket.getKey()));
					if (bucket.getDocCount() > 0) {
						map.put(ConstantsCode.value, bucket.getDocCount());
					} else {
						map.put(ConstantsCode.value, 0);
					}

					list.add(map);
				}
			}

			bean.setData(list);

		} catch (Exception e) {
			logger.debug(e.getMessage());
			return null;
		}
		return bean;

	}

	/**
	 * 近X天接口请求TOP百分比
	 * 
	 */

	public RecentWarnBean getInterFaceTopRate(int day) {
		RecentWarnBean bean = new RecentWarnBean();
		bean.setDuration(day);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {

			// 根据node_code 分组,并且取前5
			TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(ConstantsCode.node_code)
					.field(ConstantsCode.node_code_keyword);

			// 根据消耗时间AVG
			AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg(ConstantsCode.interval_sum)
					.field(ConstantsCode.interval);

			// 分组后AVG，并且根据分组 倒序
			termsAggregationBuilder.subAggregation(avgAggregationBuilder)
					.order(Order.aggregation(ConstantsCode.interval_sum, false));

			// 设置查询条件
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 当天时间开始
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(sdf.parse(dateNow).getTime() - day * 24 * 60 * 60 * 1000L).to(System.currentTimeMillis()));
			
			queryBuilder = queryBuilder.mustNot(QueryBuilders.matchQuery(ConstantsCode.node_code, "default"));

			logger.debug(queryBuilder.toString());

			// 分组取最高的前五请求时长
			SearchResponse response = transportClient.prepareSearch(elasticConfig.getApplicationIndex())
					.setTypes(elasticConfig.getChainType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.addAggregation(termsAggregationBuilder).setQuery(queryBuilder).execute().actionGet();

			// 取总接口时长
//			SearchResponse response2 = transportClient.prepareSearch(elasticConfig.getApplicationIndex())
//					.setTypes(elasticConfig.getChainType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
//					.addAggregation(avgAggregationBuilder).setQuery(queryBuilder).execute().actionGet();
			// 获取七天内的总接口请求时长
//			InternalAvg sum = response2.getAggregations().get(ConstantsCode.interval_sum);
//			Double totle = sum.getValue();
			Aggregations aggregations = response.getAggregations();
			if (aggregations != null) {
				Terms terms = aggregations.get(ConstantsCode.node_code);
				for (Terms.Bucket bucket : terms.getBuckets()) {
					logger.debug("{},{}", bucket.getKey(), bucket.getDocCount());

					Map<String, Object> map = new HashMap<String, Object>();
					map.put(ConstantsCode.name, String.valueOf(bucket.getKey()));

					InternalAvg sum2 = bucket.getAggregations().get(ConstantsCode.interval_sum);

					if (sum2.getValue() > 0) {
						map.put(ConstantsCode.value, df.format((sum2.getValue()/100000000)));
					} else {
						map.put(ConstantsCode.value, 0);
					}
					list.add(map);
				}
			}
			bean.setData(list);

		} catch (Exception e) {
			logger.debug(e.getMessage());
			return null;
		}
		return bean;
	}

	/**
	 * 主机资源消耗
	 * 
	 * @param minutes
	 *            分钟
	 */
	public List<RecentHostBean> getHostResourcesConsume(int mins) {

		List<RecentHostBean> listBean = new ArrayList<RecentHostBean>();
		try {
			// 存放KYE->ip,Values CUP消耗的LISTMAP
			Map<String, List<Map<String, Object>>> listMap = new HashMap<String, List<Map<String, Object>>>();

			Map<String, Double> map = new HashMap<String, Double>();

			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
			// 当天时间开始
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(System.currentTimeMillis() - timeGTM - mins * minFormSS).to(System.currentTimeMillis()));

			logger.debug(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex())
					.setTypes(elasticConfig.getHostType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.setQuery(queryBuilder).execute().actionGet();
			SearchHit[] hits = response.getHits().getHits();
			for (SearchHit hit : hits) {
				Map<String, Object> hitmap = hit.getSource();

				if (hitmap.containsKey(ConstantsCode.host_ip) && hitmap.containsKey(ConstantsCode.processor_load_1)) {

					try {
						String ip = String.valueOf(hitmap.get(ConstantsCode.host_ip));

						double available_memory = 0.00;
						if (!StringUtil.isNullOrEmpty(String.valueOf(hitmap.get(ConstantsCode.processor_load_1)))) {
							available_memory = Double
									.valueOf(String.valueOf(hitmap.get(ConstantsCode.processor_load_1)));
						}

						double use_memory = available_memory;

						Long time = DateFormatUtil.DateFormatGMT(String.valueOf(hitmap.get(ConstantsCode.time)),
								DateFormatUtil.GTMFormatMilSSS);

						if (listMap.containsKey(ip)) {
							List<Map<String, Object>> cupUsedList = listMap.get(ip);
							Map<String, Object> cupUsedMap = new HashMap<String, Object>();
							cupUsedMap.put(ConstantsCode.time, time);
							cupUsedMap.put(ConstantsCode.value, use_memory);
							cupUsedList.add(cupUsedMap);
							listMap.put(ip, cupUsedList);
						} else {
							List<Map<String, Object>> cupUsedList = new ArrayList<Map<String, Object>>();
							Map<String, Object> cupUsedMap = new HashMap<String, Object>();
							cupUsedMap.put(ConstantsCode.time, time);
							cupUsedMap.put(ConstantsCode.value, use_memory);
							cupUsedList.add(cupUsedMap);
							listMap.put(ip, cupUsedList);

						}

						// map用于存储的IP 以及CPU消耗总和，便于取前5
						if (map.containsKey(ip)) {
							use_memory = use_memory + map.get(ip);
							map.put(ip, use_memory);
						} else {
							map.put(ip, use_memory);
						}

					} catch (Exception e) {
						logger.debug("主机资源消耗取值异常" + e.getMessage());
					}

				}

			}
			// 根据用掉的最大的前5条IP
			map = getTop5IPUsed(map);

			// 根据用掉内存最大的前5条IP，筛选数据
			listBean = getTopUsedInfo(map, listMap);

		} catch (Exception e) {
			logger.debug(e.getMessage());
			return listBean;
		}

		return listBean;
	}

	@SuppressWarnings("rawtypes")
	private List<RecentHostBean> getTopCupUsedInfo(Map<String, Long> map,
			Map<String, List<Map<String, Object>>> listMap) {
		List<RecentHostBean> listBean = new ArrayList<RecentHostBean>();

		Iterator it = listMap.keySet().iterator();
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			if (!map.containsKey(key)) {
				it.remove();
			} else {
				RecentHostBean bean = new RecentHostBean();
				bean.setIp(key);
				bean.setData(listMap.get(key));
				listBean.add(bean);
			}
		}

		return listBean;

	}

	@SuppressWarnings("unused")
	private Map<String, Long> getTop5IPCupUsed(Map<String, Long> map) {

		Map<String, Long> result = new LinkedHashMap<>();

		Stream<Entry<String, Long>> st = map.entrySet().stream();

		st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));

		Iterator<Entry<String, Long>> it = result.entrySet().iterator();

		while (it.hasNext()) {
			if (result.size() > 5) {
				Entry<String, Long> entry = it.next();
				it.remove();
			} else {
				break;
			}
		}
		return result;
	}

	/**
	 * 磁盘总空间 / 利用率
	 * 
	 * 
	 */
	public List<RecentHostBean> getHostDiskUsed(int mins) {

		List<RecentHostBean> listBean = new ArrayList<RecentHostBean>();
		try {
			// 存放KYE->ip,Values DISK消耗的LISTMAP
			Map<String, List<Map<String, Object>>> listMap = new HashMap<String, List<Map<String, Object>>>();

			Map<String, Double> map = new HashMap<String, Double>();

			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
			// 当天时间开始
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(System.currentTimeMillis() - timeGTM - mins * minFormSS).to(System.currentTimeMillis()));

			logger.debug(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex())
					.setTypes(elasticConfig.getHostType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.setQuery(queryBuilder).execute().actionGet();
			SearchHit[] hits = response.getHits().getHits();
			for (SearchHit hit : hits) {
				Map<String, Object> hitmap = hit.getSource();

				if (hitmap.containsKey(ConstantsCode.host_ip) && hitmap.containsKey(ConstantsCode.totall_disk)
						&& hitmap.containsKey(ConstantsCode.used_disk)) {

					try {
						String ip = String.valueOf(hitmap.get(ConstantsCode.host_ip));

						Long totall_disk = 0L;
						Long used_disk = 0L;
						double userRate = 0.00;

						if (!StringUtil.isNullOrEmpty(String.valueOf(hitmap.get(ConstantsCode.totall_disk)))) {
							totall_disk = Long.valueOf(String.valueOf(hitmap.get(ConstantsCode.totall_disk)));
						}
						if (!StringUtil.isNullOrEmpty(String.valueOf(hitmap.get(ConstantsCode.used_disk)))) {
							used_disk = Long.valueOf(String.valueOf(hitmap.get(ConstantsCode.used_disk)));
						}

						if (totall_disk != 0) {
							userRate = (double) used_disk / totall_disk;
						}

						Long time = DateFormatUtil.DateFormatGMT(String.valueOf(hitmap.get(ConstantsCode.time)),
								DateFormatUtil.GTMFormatMilSSS);

						if (listMap.containsKey(ip)) {
							List<Map<String, Object>> diskList = listMap.get(ip);
							Map<String, Object> diskMap = new HashMap<String, Object>();
							diskMap.put(ConstantsCode.time, time);
							diskMap.put(ConstantsCode.total, totall_disk);
							diskMap.put(ConstantsCode.use, used_disk);
							diskList.add(diskMap);
							listMap.put(ip, diskList);
						} else {
							List<Map<String, Object>> diskList = new ArrayList<Map<String, Object>>();
							Map<String, Object> diskMap = new HashMap<String, Object>();
							diskMap.put(ConstantsCode.time, time);
							diskMap.put(ConstantsCode.total, totall_disk);
							diskMap.put(ConstantsCode.use, used_disk);
							diskList.add(diskMap);
							listMap.put(ip, diskList);

						}

						// map用于存储的IP 以及CPU消耗总和，便于取前5
						if (map.containsKey(ip)) {
							userRate = userRate + (double) map.get(ip);
							map.put(ip, userRate);
						} else {
							map.put(ip, userRate);
						}

					} catch (Exception e) {
						logger.debug("主机资源消耗取值异常" + e.getMessage());
					}

				}

			}
			// 根据用掉的最大的前5条IP
			map = getTop5IPUsed(map);

			// 根据用掉内存最大的前5条IP，筛选数据
			listBean = getTopUsedInfo(map, listMap);

		} catch (Exception e) {
			logger.debug(e.getMessage());
			return listBean;
		}

		return listBean;
	}

	@SuppressWarnings("rawtypes")
	private List<RecentHostBean> getTopUsedInfo(Map<String, Double> map,
			Map<String, List<Map<String, Object>>> listMap) {
		List<RecentHostBean> listBean = new ArrayList<RecentHostBean>();

		Iterator it = listMap.keySet().iterator();
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			if (!map.containsKey(key)) {
				it.remove();
			} else {
				RecentHostBean bean = new RecentHostBean();
				bean.setIp(key);
				bean.setData(listMap.get(key));
				listBean.add(bean);
			}
		}

		return listBean;

	}

	@SuppressWarnings("unused")
	private Map<String, Double> getTop5IPUsed(Map<String, Double> map) {

		Map<String, Double> result = new LinkedHashMap<>();

		Stream<Entry<String, Double>> st = map.entrySet().stream();

		st.sorted(Comparator.comparingDouble(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));

		Iterator<Entry<String, Double>> it = result.entrySet().iterator();

		while (it.hasNext()) {
			if (result.size() > 5) {
				Entry<String, Double> entry = it.next();
				it.remove();
			} else {
				break;
			}
		}
		return result;
	}

	/**
	 * 网络流量
	 */

	public List<RecentHostBean> getNetFlow(int mins) {
		List<RecentHostBean> listBean = new ArrayList<RecentHostBean>();
		try {
			// 存放KYE->ip,Values -> 网络流量的LISTMAP
			Map<String, List<Map<String, Object>>> listMap = new HashMap<String, List<Map<String, Object>>>();

			Map<String, Long> map = new HashMap<String, Long>();

			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
			// 当天时间开始
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(System.currentTimeMillis() - timeGTM - mins * minFormSS).to(System.currentTimeMillis()));

			logger.debug(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex())
					.setTypes(elasticConfig.getHostType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.setQuery(queryBuilder).execute().actionGet();
			SearchHit[] hits = response.getHits().getHits();
			for (SearchHit hit : hits) {
				Map<String, Object> hitmap = hit.getSource();

				if (hitmap.containsKey(ConstantsCode.host_ip) && hitmap.containsKey(ConstantsCode.incoming)
						&& hitmap.containsKey(ConstantsCode.outgoing)) {

					try {
						String ip = String.valueOf(hitmap.get(ConstantsCode.host_ip));

						Long incoming = 0L;
						Long outgoing = 0L;
						if (!StringUtil.isNullOrEmpty(String.valueOf(hitmap.get(ConstantsCode.incoming)))) {
							incoming = Long.valueOf(String.valueOf(hitmap.get(ConstantsCode.incoming)));
						}
						if (!StringUtil.isNullOrEmpty(String.valueOf(hitmap.get(ConstantsCode.outgoing)))) {
							outgoing = Long.valueOf(String.valueOf(hitmap.get(ConstantsCode.outgoing)));
						}
						Long totleing = incoming + outgoing;

						Long time = DateFormatUtil.DateFormatGMT(String.valueOf(hitmap.get(ConstantsCode.time)),
								DateFormatUtil.GTMFormatMilSSS);

						if (listMap.containsKey(ip)) {
							List<Map<String, Object>> netFlowList = listMap.get(ip);
							Map<String, Object> cupUsedMap = new HashMap<String, Object>();
							cupUsedMap.put(ConstantsCode.time, time);
							cupUsedMap.put(ConstantsCode.in, incoming);
							cupUsedMap.put(ConstantsCode.out, outgoing);
							netFlowList.add(cupUsedMap);
							listMap.put(ip, netFlowList);
						} else {
							List<Map<String, Object>> netFlowList = new ArrayList<Map<String, Object>>();
							Map<String, Object> netFlowdMap = new HashMap<String, Object>();
							netFlowdMap.put(ConstantsCode.time, time);
							netFlowdMap.put(ConstantsCode.in, incoming);
							netFlowdMap.put(ConstantsCode.out, outgoing);
							netFlowList.add(netFlowdMap);
							listMap.put(ip, netFlowList);

						}

						// map用于存储的IP 以及网络流量总和，便于取前5
						if (map.containsKey(ip)) {
							totleing = totleing + map.get(ip);
							map.put(ip, totleing);
						} else {
							map.put(ip, totleing);
						}

					} catch (Exception e) {
						logger.debug("主机资源消耗取值异常" + e.getMessage());
					}
				}
			}
			// 根据网络流量最大的前5条IP
			map = getTop5IPCupUsed(map);

			// 根据网络流量最大的前5条IP，筛选数据
			listBean = getTopCupUsedInfo(map, listMap);

		} catch (Exception e) {
			logger.debug(e.getMessage());
			return listBean;
		}

		return listBean;
	}

	/**
	 * 内存
	 */

	public List<RecentHostBean> getHostCpuUsedRate(int mins) {
		List<RecentHostBean> listBean = new ArrayList<RecentHostBean>();
		try {
			// 存放KYE->ip,Values CUP消耗的LISTMAP
			Map<String, List<Map<String, Object>>> listMap = new HashMap<String, List<Map<String, Object>>>();

			Map<String, Double> map = new HashMap<String, Double>();

			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
			// 当天时间开始
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// String dateNow = sdf.format(new Date());
			// 当前时间范围
			queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time)
					.from(System.currentTimeMillis() - timeGTM - mins * minFormSS).to(System.currentTimeMillis()));

			logger.debug(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex())
					.setTypes(elasticConfig.getHostType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
					.setQuery(queryBuilder).execute().actionGet();
			SearchHit[] hits = response.getHits().getHits();
			for (SearchHit hit : hits) {
				Map<String, Object> hitmap = hit.getSource();

				if (hitmap.containsKey(ConstantsCode.host_ip) && hitmap.containsKey(ConstantsCode.available_memory)
						&& hitmap.containsKey(ConstantsCode.total_memory)) {

					try {
						String ip = String.valueOf(hitmap.get(ConstantsCode.host_ip));
						double available_memory = 0.00;
						double total_memory = 0.00;
						double useRate = 0.00;
						if (!StringUtil.isNullOrEmpty(String.valueOf(hitmap.get(ConstantsCode.available_memory)))) {
							available_memory = Double.valueOf(String.valueOf(hitmap.get(ConstantsCode.available_memory)));
						}

						if (!StringUtil.isNullOrEmpty(String.valueOf(hitmap.get(ConstantsCode.total_memory)))) {
							total_memory = Double.valueOf(String.valueOf(hitmap.get(ConstantsCode.total_memory)));
						}
						double use_memory = total_memory - available_memory;

						if (total_memory != 0) {
							useRate = (double) use_memory / total_memory;
						}

						Long time = DateFormatUtil.DateFormatGMT(String.valueOf(hitmap.get(ConstantsCode.time)),
								DateFormatUtil.GTMFormatMilSSS);

						if (listMap.containsKey(ip)) {
							List<Map<String, Object>> cupUsedList = listMap.get(ip);
							Map<String, Object> cupUsedMap = new HashMap<String, Object>();
							cupUsedMap.put(ConstantsCode.time, time);
							cupUsedMap.put(ConstantsCode.total, total_memory);
							cupUsedMap.put(ConstantsCode.use, use_memory);
							cupUsedList.add(cupUsedMap);
							listMap.put(ip, cupUsedList);
						} else {
							List<Map<String, Object>> cupUsedList = new ArrayList<Map<String, Object>>();
							Map<String, Object> cupUsedMap = new HashMap<String, Object>();
							cupUsedMap.put(ConstantsCode.time, time);
							cupUsedMap.put(ConstantsCode.total, total_memory);
							cupUsedMap.put(ConstantsCode.use, use_memory);
							cupUsedList.add(cupUsedMap);
							listMap.put(ip, cupUsedList);

						}

						// map用于存储的IP 以及CPU消耗总和，便于取前5
						if (map.containsKey(ip)) {
							useRate = useRate + (double) map.get(ip);
							map.put(ip, useRate);
						} else {
							map.put(ip, useRate);
						}

					} catch (Exception e) {
						logger.debug("内存使用率取值异常" + e.getMessage());
					}

				}

			}
			// 根据用掉的最大的前5条IP
			map = getTop5IPUsed(map);

			// 根据用掉内存最大的前5条IP，筛选数据
			listBean = getTopUsedInfo(map, listMap);

		} catch (Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
			return listBean;
		}

		return listBean;
	}

}
