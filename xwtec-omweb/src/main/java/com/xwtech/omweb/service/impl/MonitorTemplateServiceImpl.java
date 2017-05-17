package com.xwtech.omweb.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.service.AlarmService;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.dao.MonitorTemplateMapper;
import com.xwtech.omweb.model.MonitorTemplate;
import com.xwtech.omweb.service.IMonitorTemplateService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
@Service
public class MonitorTemplateServiceImpl implements IMonitorTemplateService {

    private final static Logger logger = LoggerFactory.getLogger(MonitorTemplateServiceImpl.class);

    @Autowired
    ElasticConfig elasticConfig;

    @Autowired
    private MonitorTemplateMapper monitorTemplateMapper;

    //ES推送
    @Autowired
    private AlarmService alarmService;

    /**
     * 监听模板列表
     *
     * @param pageInfo
     * @return
     */
    @Override
    public List<MonitorTemplate> queryMonitorTemplateList(PageInfo pageInfo) {
        if(pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return monitorTemplateMapper.queryMonitorTemplateList();
    }

    /**
     * 新增监听模板
     *
     * @param monitorTemplate
     * @return
     */
    @Override
    @Transactional
    public int addMonitorTemplate(MonitorTemplate monitorTemplate) throws JsonProcessingException {
        int count = monitorTemplateMapper.addMonitorTemplate(monitorTemplate);
        if(count > 0)
        {
            count = PostParamsEs(monitorTemplate);
        }
        return count;
    }

    /**
     * 新增修改ES
     * @param monitorTemplate
     * @return
     * @throws JsonProcessingException
     */
    public int PostParamsEs(MonitorTemplate monitorTemplate) throws JsonProcessingException {
        int count = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = objectMapper.writeValueAsString(monitorTemplate);
        int status = alarmService.postCommonEs(json,elasticConfig.getMonitorTemplateType(), monitorTemplate.getTemplate_name());
        logger.info("推送变频模式ES返回状态"+status +"请求参数"+json+"请求Type"+elasticConfig.getFrequencyType());
        if (status  >= 200 && status <300)
        {
            count = 1;
        }else
        {
            count = 0;
            throw new RuntimeException("应用失败");
        }
        return count;
    }

    /**
     * 修改监听模板
     *
     * @param monitorTemplate
     * @return
     */
    @Override
    @Transactional
    public int updateMonitorTemplate(MonitorTemplate monitorTemplate) throws JsonProcessingException {
        int count = monitorTemplateMapper.updateMonitorTemplate(monitorTemplate);
        if (count > 0)
        {
            count = PostParamsEs(monitorTemplate);
        }
        return count;
    }

    /**
     * 根据监听模板主键删除监听模板
     *
     * @param monitor_template_id
     * @return
     */
    @Override
    public int deleteMonitorTemplateById(String monitor_template_id,String monitor_template_Name) {
        int count = monitorTemplateMapper.deleteMonitorTemplateById(monitor_template_id);
        if (count > 0)
        {
            int status = alarmService.deleteCommonEs(elasticConfig.getMonitorTemplateType(),monitor_template_Name);
            if (status  >= 200 && status <300)
            {
                count = 1;
            }else
            {
                count = 0;
                throw new RuntimeException("删除应用失败");
            }
        }

        return count;
    }

    /**
     * 查询监听模板详情
     *
     * @param monitor_template_id
     * @return
     */
    @Override
    public MonitorTemplate queryMonitorTemplateDetail(@Param("monitor_template_id") String monitor_template_id) {
        return monitorTemplateMapper.queryMonitorTemplateDetail(monitor_template_id);
    }

    /**
     * 查询监听模板下面是否存在监听目标
     *
     * @param monitor_template_id
     * @return
     */
    @Override
    public int queryTemplateNextTarget(@Param("monitor_template_id") String monitor_template_id) {
        return monitorTemplateMapper.queryTemplateNextTarget(monitor_template_id);
    }

    /**
     * 验证监听目标名称是否唯一
     *
     * @param monitor_template_id
     * @param template_name
     * @return
     */
    @Override
    public int validateTemplateName(@Param("monitor_template_id") String monitor_template_id, @Param("template_name") String template_name) {
        return monitorTemplateMapper.validateTemplateName(monitor_template_id,template_name);
    }
}
