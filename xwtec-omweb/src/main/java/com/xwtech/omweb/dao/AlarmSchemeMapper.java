package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.AlarmScheme;
import com.xwtech.omweb.model.AlarmSchemeUnits;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 * 告警策略数据层
 */
@Mapper
public interface AlarmSchemeMapper {


    /**
     * 查询告警策略列表
     * @return
     */
    List<AlarmScheme> queryAlarmSchemeList();

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
    int addAlarmScheme(AlarmScheme alarmScheme);

    /**
     * 修改告警策略
     * @param alarmScheme
     * @return
     */
    int updateAlarmScheme(AlarmScheme alarmScheme);

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

    List<AlarmSchemeUnits>  getUnitsList(@Param("alert_scheme_code")String alert_scheme_code);

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
     * 删除告警方案单元格
     * @param alert_scheme_code
     * @return
     */
    int deleteUnitsByCode(@Param("alert_scheme_code")String alert_scheme_code);

    /**
     * 查看告警方案单元格详情
     * @param untis_id
     * @return
     */
    AlarmSchemeUnits queryAlarmSchemeUnitsDetail(@Param("untis_id")String untis_id);

}
