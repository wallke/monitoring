package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.MetricsMapper;
import com.xwtech.omweb.model.Metrics;
import com.xwtech.omweb.service.IMetricsService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
@Service
public class MetricsServiceImpl implements IMetricsService {

    @Autowired
    private MetricsMapper metricsMapper;
    /**
     * 查询统计指标列表
     *
     * @return
     */
    @Override
    public List<Metrics> queryMetricsList(PageInfo pageInfo) {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return metricsMapper.queryMetricsList();
    }

    /**
     * 查询统计指标详情
     *
     * @param metric_id
     * @return
     */
    @Override
    public Metrics queryMetricsDetail(String metric_id) {
        return metricsMapper.queryMetricsDetail(metric_id);
    }

    /**
     * 新增统计指标
     *
     * @param metrics
     * @return
     */
    @Override
    public int addMetrics(Metrics metrics) {
        return metricsMapper.addMetrics(metrics);
    }

    /**
     * 修改统计指标
     *
     * @param metrics
     * @return
     */
    @Override
    public int updateMetrics(Metrics metrics) {
        return metricsMapper.updateMetrics(metrics);
    }

    /**
     * 删除统计指标
     *
     * @param metric_id
     * @return
     */
    @Override
    public int deleteMetricsById(String metric_id) {
        return metricsMapper.deleteMetricsById(metric_id);
    }

    /**
     * 验证指标类型是否存在
     *
     * @param metric_id
     * @param metric
     * @return
     */
    @Override
    public int validateName(@Param("metric_id") String metric_id, @Param("metric") String metric) {
        return metricsMapper.validateName(metric_id,metric);
    }
}
