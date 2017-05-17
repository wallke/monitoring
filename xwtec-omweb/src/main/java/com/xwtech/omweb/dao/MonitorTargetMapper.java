package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.Grid;
import com.xwtech.omweb.model.MonitorTarget;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 * 监听目标数据层
 */
public interface MonitorTargetMapper {

    /**
     * 查询监听目标档案列表
     * @return
     */
    List<MonitorTarget> queryMonitoringTargetList();

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
    int addMonitoringTarget(MonitorTarget monitorTarget);

    /**
     * 修改监听目标
     * @param monitorTarget
     * @return
     */
    int updateMonitoringTarget(MonitorTarget monitorTarget);

    /**
     * 删除监听目标
     * @param target_code
     * @return
     */
    int deleteMonitoringTarget(@Param("target_code")String target_code);

    /**
     * 新增对照关系表
     * @param target_code
     * @param grid_code
     * @return
     */
    int addTargetDZGrid(@Param("target_code")String target_code,@Param("grid_code")String grid_code);

    /**
     * 删除对照关系
     * @param target_code
     * @return
     */
    int deleteTargetDZGrid(@Param("target_code")String target_code);

    /**
     * 对照关系列表
     * @param target_code
     * @return
     */
    List<Grid> queryDzTargetList(@Param("target_code")String target_code);
}
