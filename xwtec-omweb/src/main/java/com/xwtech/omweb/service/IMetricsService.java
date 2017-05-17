package com.xwtech.omweb.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.Metrics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public interface IMetricsService {

    /**
     * 查询统计指标列表
     * @return
     */
    List<Metrics> queryMetricsList(PageInfo pageInfo);

    /**
     * 查询统计指标详情
     * @param metric_id
     * @return
     */
    Metrics queryMetricsDetail(String metric_id);

    /**
     * 新增统计指标
     * @param metrics
     * @return
     */
    int addMetrics(Metrics metrics);

    /**
     * 修改统计指标
     * @param metrics
     * @return
     */
    int updateMetrics(Metrics metrics);

    /**
     * 删除统计指标
     * @param metric_id
     * @return
     */
    int deleteMetricsById(String metric_id);

    /**
     * 验证指标类型是否存在
     * @param metric_id
     * @param metric
     * @return
     */
    int validateName(@Param("metric_id") String metric_id, @Param("metric") String metric);


}
