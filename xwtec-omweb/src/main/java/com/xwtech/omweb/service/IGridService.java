package com.xwtech.omweb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.service.AlarmService;
import com.xwtech.omweb.model.Grid;
import com.xwtech.omweb.model.Units;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Administrator on 2017/3/17 0017.
 */
public interface IGridService {


    /**
     * 查询预警网格列表
     * @return
     */
    List<Grid> queryGridList(PageInfo pageInfo);
    /**
     * 根据code查询预警网格详情
     * @param grid_code
     * @return
     */
    Grid queryDetailByCode(@Param("grid_code")String grid_code);

    /**
     * 新增预警网格
     * @param grid
     * @return
     */
    int addGrid(Grid grid) throws JsonProcessingException;

    /**
     * 修改预警网格
     * @param grid
     * @return
     */
    int updateGrid(Grid grid) throws JsonProcessingException;

    /**
     * 删除预警网格
     * @param grid_code
     * @return
     */
    int deleteGrid(@Param("grid_code")String grid_code);

    /**
     * 根据预警网格编码查询预警单元格列表
     * @param grid_code
     * @return
     */
    List<Units> queryUnitsListByCode(@Param("grid_code")String grid_code);

    /**
     * 查询预警单元格详情
     * @param units_id
     * @return
     */
    Units queryUnitsDetail(@Param("units_id")String units_id);

    /***
     * 新增预警单元格
     * @param units
     * @return
     */
    int addUnits(Units units);

    /**
     * 修改预警单元格
     * @param units
     * @return
     */
    int updateUnits(Units units);

    /**
     * 删除预警单元格
     * @param units_id
     * @return
     */
    int deleteUnits(@Param("units_id")String units_id);

    /**
     * 删除单元格信息根据编码
     * @param grid_code
     * @return
     */
}
