package com.xwtech.es.service;

import com.alibaba.fastjson.JSONObject;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.model.DateRange;
import com.xwtech.es.model.HealthBean;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/3/20 0020.
 * 告警相关模块推送
 */
@Service
public class AlarmService {

    private final static Logger logger = LoggerFactory.getLogger(AlarmService.class);

    @Autowired
    TransportClient transportClient;

    @Autowired
    ElasticConfig elasticConfig;



    /**
     * 应用SDK信息
     *
     * @param id
     * @param json
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public int  postSdk(String id, String json) throws ExecutionException, InterruptedException {
        IndexRequest indexRequest = new IndexRequest(elasticConfig.getXwIndex(), elasticConfig.getEsType(), id).source(json);
        UpdateRequest updateRequest = new UpdateRequest(elasticConfig.getXwIndex(), elasticConfig.getEsType(), id).doc(json).upsert(indexRequest);
        UpdateResponse updateResponse = transportClient.update(updateRequest).get();
        RestStatus status = updateResponse.status();
        int statusCode = status.getStatus();
        return statusCode;
    }

    /**
     * 更新版本时间
     *
     * @param updateDate
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public int postDate(String updateDate) throws ExecutionException, InterruptedException {
        IndexRequest indexRequest1 = new IndexRequest(elasticConfig.getXwIndex(), elasticConfig.getEsType(), elasticConfig.getEsId()).source(updateDate);
        UpdateRequest updateRequestDate = new UpdateRequest(elasticConfig.getXwIndex(), elasticConfig.getEsType(), elasticConfig.getEsId()).doc(updateDate).upsert(indexRequest1);
        UpdateResponse updateResponse = transportClient.update(updateRequestDate).get();
        int status = updateResponse.status().getStatus();
        return status;
    }
    public int updateDate(String updateDate,String type,String model) throws ExecutionException, InterruptedException {
        IndexRequest indexRequest1 = new IndexRequest(elasticConfig.getXwIndex(), type, model).source(updateDate);
        UpdateRequest updateRequestDate = new UpdateRequest(elasticConfig.getXwIndex(), type, model).doc(updateDate).upsert(indexRequest1);
        UpdateResponse updateResponse = transportClient.update(updateRequestDate).get();
        int status = updateResponse.status().getStatus();
        logger.info("一键启动返回ES状态"+status+"ESindex:"+elasticConfig.getXwIndex()+"type:"+type+"esId"+model);
        return status;
    }

    /**
     * 新增修模板至ES
     * @param postParam
     * @param type
     * @return
     */
    public int postCommonEs(String postParam,String type,String code){
        IndexRequest indexRequest1 = new IndexRequest(elasticConfig.getXwIndex(), type, code).source(postParam);
        UpdateRequest updateRequestDate = new UpdateRequest(elasticConfig.getXwIndex(), type, code).doc(postParam).upsert(indexRequest1);
        UpdateResponse updateResponse = null;
        try {
            updateResponse = transportClient.update(updateRequestDate).get();
            logger.info("新增修改Es返回数据"+updateResponse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        int status = updateResponse.status().getStatus();
        return status;
    }

    public int deleteCommonEs(String type,String code)
    {
        DeleteResponse  deleteResponse = transportClient.prepareDelete(elasticConfig.getXwIndex(), type, code).execute().actionGet();
        int status = deleteResponse.status().getStatus();
        return status;
    }

    public long queryESTime() {
        long timestamp = transportClient.admin().cluster().prepareClusterStats().get().getTimestamp();
        timestamp = timestamp/ 1000 ;
        return timestamp;
    }


    public List<HealthBean.AppBean>  queryHealths(DateRange dateRange, String app, String host){
        //聚合所有应用数据 根据应用聚合所有主机 根据主机查询当前主机节点
        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("appServer_name").field("appServer_name.keyword");
        termsAggregationBuilder.subAggregation(AggregationBuilders.terms("term_ip").field("server_ip.keyword"));

        //时间范围 必要筛选条件
        if (dateRange != null)
        {
            queryBuilder = queryBuilder.must(
                    QueryBuilders.rangeQuery("time")
                            .from(dateRange.getStart().getTime())
                            .to(dateRange.getEnd().getTime())
            );
        }
        //应用 可选条件
        if (!StringUtils.isEmpty(app))
        {
            queryBuilder.must(QueryBuilders.matchQuery("appServer_name",app));
            //主机 可选条件
            if (!StringUtils.isEmpty(host)) {
                queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_ip", host));
            }
        }
        SearchResponse response = transportClient.prepareSearch(".xwtec")
                .setTypes("health")
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .addAggregation(termsAggregationBuilder)
//                .addSort("interval", SortOrder.ASC)
                .setQuery(queryBuilder).execute().actionGet();
        logger.info(response.toString());
        Aggregations aggregations = response.getAggregations();
        //获取es服务器时间
        long esTime = queryESTime();
        List<HealthBean.AppBean> appList = new ArrayList<HealthBean.AppBean>();
        if (aggregations !=null)
        {
            SearchHit[] hits = response.getHits().getHits();
            List<HealthBean> healthList = new ArrayList<HealthBean>();
            for (SearchHit hit : hits){
                HealthBean healthBean = JSONObject.parseObject(hit.getSourceAsString(), HealthBean.class);
                healthList.add(healthBean);
            }
            Terms termServers = aggregations.get("appServer_name");
            //应用集合
            for (Terms.Bucket bucket: termServers.getBuckets())
            {
                HealthBean.AppBean appBean = new HealthBean.AppBean();
                appBean.setAppServer_name(bucket.getKeyAsString());
                logger.debug("{},{}", bucket.getKeyAsString(), bucket.getDocCount());
                //主机集合
                Aggregations aggregations1 = bucket.getAggregations();
                if (aggregations1 != null)
                {
                    List<HealthBean.HostBean> hostList = new ArrayList<HealthBean.HostBean>();
                    Terms termIps= aggregations1.get("term_ip");
                    for (Terms.Bucket termIp : termIps.getBuckets())
                    {
                        HealthBean.HostBean hostBean = new HealthBean.HostBean();
                        hostBean.setServer_ip(termIp.getKeyAsString());
                        hostBean.setPortCount((int) termIp.getDocCount());
                        List<HealthBean> collect = healthList.stream().filter(x -> x.getServer_ip().equals(termIp.getKeyAsString()) ).collect(Collectors.toList());
                        //服务器时间 - es 时间 > 1 分钟,异常 否则正常
                        int errorCount =0;
                        int successCount =0;
                        for (int i = 0; i <collect.size() ; i++) {
                            HealthBean healthBean = collect.get(i);
                            Date time = healthBean.getTime();
                            long chuTime = time.getTime()/ 1000 ;
                            logger.info(String.valueOf(esTime));
                            logger.info(String.valueOf(chuTime));

                            logger.info("时间减去"+String.valueOf(esTime-chuTime));
                            if(esTime - chuTime > 60)
                            {
                                errorCount= errorCount + 1;
                            }else
                            {
                                successCount= successCount+1;
                            }
                        }
                        hostBean.setPortList(collect);
                        hostBean.setSuccessCount(successCount);
                        hostBean.setErrorCount(errorCount);
                        hostList.add(hostBean);
                        logger.debug("{},{}", termIp.getKeyAsString(), termIp.getDocCount());
                        appBean.setHostList(hostList);
                    }
                }
                appList.add(appBean);
            }
        }
        return appList;
    }


    public List<HealthBean> queryPortDetail(String app, String host){
        //设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //应用 可选条件
        if (!StringUtils.isEmpty(app))
        {
            queryBuilder.must(QueryBuilders.matchQuery("appServer_name",app));
            //主机 可选条件
            if (!StringUtils.isEmpty(host)) {
                queryBuilder = queryBuilder.must(QueryBuilders.matchQuery("server_ip", host));
            }
        }
        SearchResponse response = transportClient.prepareSearch(".xwtec")
                .setTypes("health")
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setQuery(queryBuilder).execute().actionGet();
        logger.info(response.toString());
        SearchHit[] hits = response.getHits().getHits();
        List<HealthBean> healthList = new ArrayList<HealthBean>();
        long esTime = queryESTime();
        for (SearchHit hit : hits){
            HealthBean healthBean = JSONObject.parseObject(hit.getSourceAsString(), HealthBean.class);
            //服务器时间 - es 时间 > 1 分钟,异常 否则正常
            Date time = healthBean.getTime();
            long chuTime = time.getTime()/ 1000 ;
            logger.info(String.valueOf(esTime));
            logger.info(String.valueOf(chuTime));
            logger.info("时间减去"+String.valueOf(esTime-chuTime));
            if(esTime - chuTime > 60)
            {
                healthBean.setStatus("1");
            }else
            {
                healthBean.setStatus("0");
            }
            healthList.add(healthBean);
        }
        return healthList;
    }


}
