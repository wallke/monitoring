package com.xwtech.omweb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.MonitorTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public interface IMonitorTemplateService {


    /**
     * 监听模板列表
     * @return
     */
    List<MonitorTemplate> queryMonitorTemplateList(PageInfo pageInfo);

    /**
     * 新增监听模板
     * @param monitorTemplate
     * @return
     */
    int addMonitorTemplate(MonitorTemplate monitorTemplate) throws JsonProcessingException;

    /**
     * 修改监听模板
     * @param monitorTemplate
     * @return
     */
    int updateMonitorTemplate(MonitorTemplate monitorTemplate) throws JsonProcessingException;

    /**
     * 根据监听模板主键删除监听模板
     * @return
     */
    int deleteMonitorTemplateById(String monitor_template_id,String monitor_template_Name);

    /**
     * 查询监听模板详情
     * @return
     */
    MonitorTemplate queryMonitorTemplateDetail(@Param("monitor_template_id")String monitor_template_id);

    /**
     * 查询监听模板下面是否存在监听目标
     * @param monitor_template_id
     * @return
     */
    int queryTemplateNextTarget(@Param("monitor_template_id")String monitor_template_id);

    /**
     * 验证监听目标名称是否唯一
     * @param monitor_template_id
     * @param template_name
     * @return
     */
    int validateTemplateName(@Param("monitor_template_id")String monitor_template_id,@Param("template_name")String template_name);

}
