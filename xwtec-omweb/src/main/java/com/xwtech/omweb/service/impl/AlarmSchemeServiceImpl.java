package com.xwtech.omweb.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.service.AlarmService;
import com.xwtech.omweb.controller.AppGroupController;
import com.xwtech.omweb.dao.AlarmSchemeMapper;
import com.xwtech.omweb.model.AlarmScheme;
import com.xwtech.omweb.model.AlarmSchemeUnits;
import com.xwtech.omweb.model.Grid;
import com.xwtech.omweb.service.IAlarmSchemeService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
@Service
public class AlarmSchemeServiceImpl implements IAlarmSchemeService {

    private final static Logger logger = LoggerFactory.getLogger(AlarmSchemeServiceImpl.class);

    @Autowired
    private AlarmSchemeMapper alarmSchemeMapper;

    //ES推送
    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ElasticConfig elasticConfig;

    /**
     * 查询告警策略列表
     *
     * @return
     */
    @Override
    public List<AlarmScheme> queryAlarmSchemeList(PageInfo pageInfo) {
        if (pageInfo !=null ){
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return alarmSchemeMapper.queryAlarmSchemeList();
    }

    /**
     * 根据id查询告警策略详情
     *
     * @return
     */
    @Override
    public AlarmScheme queryAlarmSchemeDetail(String alert_scheme_code) {
        return alarmSchemeMapper.queryAlarmSchemeDetail(alert_scheme_code);
    }

    /**
     * 新增告警策略
     *
     * @param alarmScheme
     * @return
     */
    @Override
    public int addAlarmScheme(AlarmScheme alarmScheme) throws JsonProcessingException {
        int count = alarmSchemeMapper.addAlarmScheme(alarmScheme);
        return count;
    }

    /**
     * 修改告警策略
     *
     * @param alarmScheme
     * @return
     */
    @Override
    public int updateAlarmScheme(AlarmScheme alarmScheme) throws JsonProcessingException {
        int count = alarmSchemeMapper.updateAlarmScheme(alarmScheme);
        return count;
    }

    /**
     * 根据ID删除告警策略
     *
     * @param alert_scheme_code
     * @return
     */
    @Override
    public int deleteAlarmScheme(@Param("alert_scheme_code") String alert_scheme_code) {
        alarmSchemeMapper.deleteUnitsByCode(alert_scheme_code);
        int count = alarmSchemeMapper.deleteAlarmScheme(alert_scheme_code);
        if (count > 0)
        {
            count = alarmService.deleteCommonEs(elasticConfig.getSchemeType(), alert_scheme_code);
        }
        return count;
    }

    /**
     * 查询告警方案单元格列表
     *
     * @param alert_scheme_code
     * @return
     */
    @Override
    public List<AlarmSchemeUnits> queryAlarmSchemeUnitsList(@Param("alert_scheme_code") String alert_scheme_code) {
        return alarmSchemeMapper.queryAlarmSchemeUnitsList(alert_scheme_code);
    }

    /**
     * 新增告警方案单元格
     *
     * @param alarmSchemeUnits
     * @return
     */
    @Override
    public int addAlarmSchemeUnits(AlarmSchemeUnits alarmSchemeUnits) {
        return alarmSchemeMapper.addAlarmSchemeUnits(alarmSchemeUnits);
    }

    /**
     * 修改告警方案单元格
     *
     * @param alarmSchemeUnits
     * @return
     */
    @Override
    public int updateAlarmSchemeUnits(AlarmSchemeUnits alarmSchemeUnits) {
        return alarmSchemeMapper.updateAlarmSchemeUnits(alarmSchemeUnits);
    }

    /**
     * 删除告警方案单元格
     *
     * @param untis_id
     * @return
     */
    @Override
    @Transactional
    public int deleteAlarmSchemeUnits(@Param("untis_id") String untis_id) {
        return alarmSchemeMapper.deleteAlarmSchemeUnits(untis_id);
    }

    /**
     * 查看告警方案单元格详情
     *
     * @param untis_id
     * @return
     */
    @Override
    public AlarmSchemeUnits queryAlarmSchemeUnitsDetail(@Param("untis_id") String untis_id) {
        return alarmSchemeMapper.queryAlarmSchemeUnitsDetail(untis_id);
    }
}
