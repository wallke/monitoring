package com.xwtech.omweb.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.service.AlarmService;
import com.xwtech.omweb.dao.FrequencyModeMapper;
import com.xwtech.omweb.model.FrequencyMode;
import com.xwtech.omweb.model.Grid;
import com.xwtech.omweb.service.IFrequencyModeService;
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
public class FrequencyModeServiceImpl implements IFrequencyModeService {

    @Autowired
    private FrequencyModeMapper frequencyModeMapper;

    //ES推送
    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ElasticConfig elasticConfig;

    private final static Logger logger = LoggerFactory.getLogger(FrequencyModeServiceImpl.class);

    /**
     * 查询变频模式列表
     *
     * @param pageInfo
     * @return
     */
    @Override
    public List<FrequencyMode> queryFrequencyMode(PageInfo pageInfo) {
        if (pageInfo !=null){
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return frequencyModeMapper.queryFrequencyMode();
    }

    /**
     * 根据id查询变频模式详情
     *
     * @param frequency_code
     * @return
     */
    @Override
    public FrequencyMode queryFrequencyModeDetail(@Param("frequency_code") String frequency_code) {
        return frequencyModeMapper.queryFrequencyModeDetail(frequency_code);
    }

    @Transactional
    public int PostParamsEs(FrequencyMode frequencyMode) throws JsonProcessingException {
        int count = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        String interval = frequencyMode.getInterval();
        String[] split = interval.split(",");
        int[] ints = new int[split.length];
        for(int i=0;i<split.length;i++)
        {
            ints[i] = Integer.parseInt(split[i]);
        }
        frequencyMode.setInterval_seq(ints);
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = objectMapper.writeValueAsString(frequencyMode);
        int status = alarmService.postCommonEs(json,elasticConfig.getFrequencyType(),frequencyMode.getFrequency_code());
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
     * 新增变频模式
     *
     * @param frequencyMode
     * @return
     */
    @Override
    public int addFrequencyMode(FrequencyMode frequencyMode) throws JsonProcessingException {
        int count = frequencyModeMapper.addFrequencyMode(frequencyMode);
        if (count > 0)
        {
            count = PostParamsEs(frequencyMode);
        }
        return count;
    }

    /**
     * 修改变频模式
     *
     * @param frequencyMode
     * @return
     */
    @Override
    public int updateFrequencyMode(FrequencyMode frequencyMode) throws JsonProcessingException {
        int count = frequencyModeMapper.updateFrequencyMode(frequencyMode);
        if(count > 0)
        {
            count = PostParamsEs(frequencyMode);
        }
        return count;
    }

    /**
     * 根据主键删除变频模式
     *
     * @param frequency_code
     * @return
     */
    @Override
    public int deleteFrequencyMode(@Param("frequency_code") String frequency_code) {
        int count = frequencyModeMapper.deleteFrequencyMode(frequency_code);
        if (count > 0)
        {
            count = alarmService.deleteCommonEs(elasticConfig.getFrequencyType(), frequency_code);
        }
        return count;
    }

    /**
     * 验证变频名称是否重复
     *
     * @param frequency_code
     * @param frequency_name
     * @return
     */
    @Override
    public int validateFrequencyName(@Param("frequency_code") String frequency_code, @Param("frequency_name") String frequency_name) {
        return frequencyModeMapper.validateFrequencyName(frequency_code,frequency_name);
    }
}
