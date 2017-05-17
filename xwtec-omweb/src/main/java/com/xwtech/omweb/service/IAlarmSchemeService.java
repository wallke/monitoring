package com.xwtech.omweb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.AlarmScheme;
import com.xwtech.omweb.model.AlarmSchemeUnits;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
public interface IAlarmSchemeService {


    /**
     * 查询告警策略列表
     * @return
     */
    List<AlarmScheme> queryAlarmSchemeList(PageInfo pageInfo);

    /**
     * 根据id查询告警策略详情
     * @return
     */
    AlarmScheme queryAlarmSchemeDetail(@Param("alert_scheme_code")String id);

    /**
     * 新增告警策略
     * @param alarmScheme
     * @return
     */
    int addAlarmScheme(AlarmScheme alarmScheme) throws JsonProcessingException;

    /**
     * 修改告警策略
     * @param alarmScheme
     * @return
     */
    int updateAlarmScheme(AlarmScheme alarmScheme) throws JsonProcessingException;

    /**
     * 根据ID删除告警策略
     * @param id
     * @return
     */
    int deleteAlarmScheme(@Param("alert_scheme_code")String id);

    /**
     * 查询告警方案单元格列表
     * @param alert_scheme_code
     * @return
     */
    List<AlarmSchemeUnits> queryAlarmSchemeUnitsList(@Param("alert_scheme_code")String alert_scheme_code);

    /**
     * 新增告警方案单元格
     * @param alarmSchemeUnits
     * @return
     */
    int addAlarmSchemeUnits(AlarmSchemeUnits alarmSchemeUnits);

    /**
     * 修改告警方案单元格
     * @param alarmSchemeUnits
     * @return
     */
    int updateAlarmSchemeUnits(AlarmSchemeUnits alarmSchemeUnits);

    /**
     * 删除告警方案单元格
     * @param untis_id
     * @return
     */
    int deleteAlarmSchemeUnits(@Param("untis_id")String untis_id);

    /**
     * 查看告警方案单元格详情
     * @param untis_id
     * @return
     */
    AlarmSchemeUnits queryAlarmSchemeUnitsDetail(@Param("untis_id")String untis_id);



}
