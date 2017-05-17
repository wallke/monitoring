package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.FrequencyMode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 * 变频模式数据层
 */
@Mapper
public interface FrequencyModeMapper {


    /**
     * 查询变频模式列表
     * @return
     */
    List<FrequencyMode> queryFrequencyMode();

    /**
     * 根据id查询变频模式详情
     * @param frequency_code
     * @return
     */
    FrequencyMode queryFrequencyModeDetail(@Param("frequency_code") String frequency_code);
    /**
     * 新增变频模式
     * @param frequencyMode
     * @return
     */
    int addFrequencyMode(FrequencyMode frequencyMode);

    /**
     * 修改变频模式
     * @param frequencyMode
     * @return
     */
    int updateFrequencyMode(FrequencyMode frequencyMode);

    /**
     * 根据主键删除变频模式
     * @param frequency_code
     * @return
     */
    int deleteFrequencyMode(@Param("frequency_code") String frequency_code);

    /**
     * 验证变频名称是否重复
     * @param frequency_code
     * @param frequency_name
     * @return
     */
    int validateFrequencyName(@Param("frequency_code") String frequency_code,@Param("frequency_name")String frequency_name);


}
