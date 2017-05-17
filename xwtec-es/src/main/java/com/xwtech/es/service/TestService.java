package com.xwtech.es.service;

import com.xwtech.es.ElasticConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentile;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesMethod;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangq on 2017/2/14.
 */
@Service
public class TestService {

    static Logger logger = LoggerFactory.getLogger(TestService.class);


    @Autowired
    TransportClient transportClient;


    @Autowired
    ElasticConfig elasticConfig;

    public void get(){
        //设置查询条件
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        System.out.println(queryBuilder.toString());
        PercentilesAggregationBuilder aggregation =
                AggregationBuilders.percentiles("agg")
                        .field("interval")
                        .method(PercentilesMethod.HDR)
                        .numberOfSignificantValueDigits(5)
                        .percentiles(50, 70.0,80, 90.0, 100.0) ;

        SearchResponse response =
                transportClient.prepareSearch("xwtec_application-*")
                        .setTypes("xwtec_application_transaction")
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        //.setSource()
                        .addAggregation(aggregation)
                        //.addAggregation(dateHistogramAggregationBuilder)
                        .setQuery(queryBuilder)
                        .addSort("interval", SortOrder.ASC).execute().actionGet();
        Percentiles agg = response.getAggregations().get("agg");
        for (Percentile entry : agg) {
            double percent = entry.getPercent();    // Percent
            double value = entry.getValue();        // Value
            logger.info("percent [{}], value [{}]", percent, value);
        }
//
//            InternalDateHistogram agg2 = response.getAggregations().get("agg2");
//
//
//            for (Histogram.Bucket entry : agg2.getBuckets()) {
//
//                Aggregations aggregations = entry.getAggregations();
//
//
//               // logger.info("percent [{}], value [{}]", percent, value);
//            }

        int i = 0;

        for (SearchHit hit : response.getHits().getHits()) {
            //System.out.println("---->>hit.getId(): " + hit.getId());

            String json = hit.getSourceAsString();

            logger.warn(json);

            logger.warn((++i) + "、interval: " + hit.getSource().get("interval"));
            //System.out.println("interval: " + hit.getSource().get("interval"));
        }
    }

}
