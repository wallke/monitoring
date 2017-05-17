package com.xwtech.omweb.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.service.AlarmService;
import com.xwtech.omweb.dao.GridMapper;
import com.xwtech.omweb.model.Grid;
import com.xwtech.omweb.model.Units;
import com.xwtech.omweb.service.IGridService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/3/17 0017.
 */
@Service
public class GridServiceImpl implements IGridService {

    @Autowired
    private GridMapper gridMapper;

    //ES推送
    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ElasticConfig elasticConfig;
    /**
     * 查询预警网格列表
     *
     * @param pageInfo
     * @return
     */
    @Override
    public List<Grid> queryGridList(PageInfo pageInfo) {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return gridMapper.queryGridList();
    }

    /**
     * 根据code查询预警网格详情
     *
     * @param grid_code
     * @return
     */
    @Override
    public Grid queryDetailByCode(@Param("grid_code") String grid_code) {
        return gridMapper.queryDetailByCode(grid_code);
    }


    /**
     * 新增预警网格
     *
     * @param grid
     * @return
     */
    @Override
    @Transactional
    public int addGrid(Grid grid) throws JsonProcessingException {
        int count = gridMapper.addGrid(grid);
        return count;
    }

    /**
     * 修改预警网格
     *
     * @param grid
     * @return
     */
    @Override
    @Transactional
    public int updateGrid(Grid grid) throws JsonProcessingException {
        int count = gridMapper.updateGrid(grid);
        return count;
    }

    /**
     * 删除预警网格
     *
     * @param grid_code
     * @return
     */
    @Override
    @Transactional
    public int deleteGrid(@Param("grid_code") String grid_code) {
        gridMapper.deleteUnitsByCode(grid_code);
        int count = gridMapper.deleteGrid(grid_code);
        if(count > 0)
        {
            count = alarmService.deleteCommonEs(elasticConfig.getThresholdType(),grid_code);
        }
        return count;
    }

    /**
     * 根据预警网格编码查询预警单元格列表
     *
     * @param grid_code
     * @return
     */
    @Override
    public List<Units> queryUnitsListByCode(@Param("grid_code") String grid_code) {
        return gridMapper.queryUnitsListByCode(grid_code);
    }

    /**
     * 查询预警单元格详情
     *
     * @param units_id
     * @return
     */
    @Override
    public Units queryUnitsDetail(@Param("units_id") String units_id) {
        return gridMapper.queryUnitsDetail(units_id);
    }

    /***
     * 新增预警单元格
     * @param units
     * @return
     */
    @Override
    public int addUnits(Units units) {
        return gridMapper.addUnits(units);
    }

    /**
     * 修改预警单元格
     *
     * @param units
     * @return
     */
    @Override
    public int updateUnits(Units units) {
        return gridMapper.updateUnits(units);
    }

    /**
     * 删除预警单元格
     *
     * @param units_id
     * @return
     */
    @Override
    public int deleteUnits(@Param("units_id") String units_id) {
        return gridMapper.deleteUnits(units_id);
    }
}
