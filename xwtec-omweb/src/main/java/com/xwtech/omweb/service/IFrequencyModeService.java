package com.xwtech.omweb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.FrequencyMode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
public interface IFrequencyModeService {

    /**
     * 查询变频模式列表
     * @return
     */
    List<FrequencyMode> queryFrequencyMode(PageInfo pageInfo);

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
    int addFrequencyMode(FrequencyMode frequencyMode) throws JsonProcessingException;

    /**
     * 修改变频模式
     * @param frequencyMode
     * @return
     */
    int updateFrequencyMode(FrequencyMode frequencyMode) throws JsonProcessingException;

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
