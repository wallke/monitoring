package com.xwtech.omweb.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.service.AlarmService;
import com.xwtech.omweb.dao.MonitorTargetMapper;
import com.xwtech.omweb.model.Grid;
import com.xwtech.omweb.model.MonitorTarget;
import com.xwtech.omweb.service.IMonitorTargetService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
@Service
public class MonitorTargetServiceImpl implements IMonitorTargetService {

    private final static Logger logger = LoggerFactory.getLogger(MonitorTargetServiceImpl.class);
    @Autowired
    private MonitorTargetMapper monitoringTargetMapper;

    //ES推送
    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ElasticConfig elasticConfig;

    /**
     * 查询监听目标档案列表
     *
     * @return
     */
    @Override
    public List<MonitorTarget> queryMonitoringTargetList(PageInfo pageInfo) {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return monitoringTargetMapper.queryMonitoringTargetList();
    }

    /**
     * 查询监听目标档案详情
     *
     * @param target_code
     * @return
     */
    @Override
    public MonitorTarget queryMonitoringTargetDetail(@Param("target_code") String target_code) {
        return monitoringTargetMapper.queryMonitoringTargetDetail(target_code);
    }

    /**
     * 新增监听目标
     *
     * @param monitorTarget
     * @return
     */
    @Override
    @Transactional
    public int addMonitoringTarget(MonitorTarget monitorTarget) throws JsonProcessingException {
        String[] grid_code = monitorTarget.getGrid_code();
        if (grid_code !=null && grid_code.length>0)
        {
            //新增对照关系
            for (String code: grid_code) {
                monitoringTargetMapper.addTargetDZGrid(monitorTarget.getTarget_code(),code);
            }
        }
        int count = monitoringTargetMapper.addMonitoringTarget(monitorTarget);
        if (count >0 )
        {
            count = PostParamsEs(monitorTarget);
        }
        return count;
    }

    /**
     * 新增修改ES
     * @param monitorTarget
     * @return
     * @throws JsonProcessingException
     */
    @Transactional
    public int PostParamsEs(MonitorTarget monitorTarget) throws JsonProcessingException {
        int count = 0;
        MonitorTarget target = this.queryMonitoringTargetDetail(monitorTarget.getTarget_code());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = objectMapper.writeValueAsString(target);
        int status = alarmService.postCommonEs(json,elasticConfig.getMonitorTargetType(),monitorTarget.getTarget_name());
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
     * 修改监听目标
     *
     * @param monitorTarget
     * @return
     */
    @Override
    public int updateMonitoringTarget(MonitorTarget monitorTarget) throws JsonProcessingException {
        int count = monitoringTargetMapper.updateMonitoringTarget(monitorTarget);
        //删除对照关系
        monitoringTargetMapper.deleteTargetDZGrid(monitorTarget.getTarget_code());
        //新增对照关系
        String[] grid_code = monitorTarget.getGrid_code();
        if (grid_code !=null && grid_code.length>0)
        {
            for (String code: grid_code) {
                monitoringTargetMapper.addTargetDZGrid(monitorTarget.getTarget_code(),code);
            }
        }

        if (count >0 )
        {
            //删除ES照片
            alarmService.deleteCommonEs("monitorTargetType",monitorTarget.getTarget_code());
            count = PostParamsEs(monitorTarget);
        }
        return count;
    }

    /**
     * 删除监听目标
     *
     * @param target_code
     * @return
     */
    @Override
    @Transactional
    public int deleteMonitoringTarget(String target_code,String target_name) {
        int count = monitoringTargetMapper.deleteMonitoringTarget(target_code);
        if (count > 0)
        {
            int status = alarmService.deleteCommonEs(elasticConfig.getMonitorTargetType(),target_name);
            if (status  >= 200 && status <300)
            {
                count = 1;
            }else
            {
                count = 0;
                throw new RuntimeException("应用失败");
            }
        }
        return count;
    }
}
