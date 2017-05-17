package com.xwtech.omweb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.MonitorTarget;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public interface IMonitorTargetService {



    /**
     * 查询监听目标档案列表
     * @return
     */
    List<MonitorTarget> queryMonitoringTargetList(PageInfo pageInfo);

    /**
     * 查询监听目标档案详情
     * @param target_code
     * @return
     */
    MonitorTarget queryMonitoringTargetDetail(@Param("target_code")String target_code);

    /**
     * 新增监听目标
     * @param monitorTarget
     * @return
     */
    int addMonitoringTarget(MonitorTarget monitorTarget) throws JsonProcessingException;

    /**
     * 修改监听目标
     * @param monitorTarget
     * @return
     */
    int updateMonitoringTarget(MonitorTarget monitorTarget) throws JsonProcessingException;

    /**
     * 删除监听目标
     * @param target_code
     * @return
     */
    int deleteMonitoringTarget(String target_code,String target_name);
}
