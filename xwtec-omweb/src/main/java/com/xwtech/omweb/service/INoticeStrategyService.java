package com.xwtech.omweb.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.ContactsChannel;
import com.xwtech.omweb.model.Grid;
import com.xwtech.omweb.model.NoticeStrategy;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/3/14 0014.
 */
public interface INoticeStrategyService {


    /**
     * 查看通知策略集合
     * @return
     */
    List<NoticeStrategy> queryNoticeStrategyList(PageInfo pageInfo);

    /**
     * 查询通知策略详情
     * @param notice_strategy_code
     * @return
     */
    NoticeStrategy queryNoticeStrategyDetail(@Param("notice_strategy_code")String notice_strategy_code);

    /**
     * 新增通知策略
     * @param noticeStrategy
     * @return
     */
    int addNoticeStrategy(NoticeStrategy noticeStrategy,String[] frequencyList,String[] channelIdLists) throws JsonProcessingException;

    /**
     * 修改通知策略
     * @param noticeStrategy
     * @return
     */
    int updateNoticeStrategy(NoticeStrategy noticeStrategy,String[] frequencyList,String[] channelIdLists) throws JsonProcessingException;

    /**
     * 删除通知策略
     * @param notice_strategy_code
     * @return
     */
    int deleteNoticeStrategy(@Param("notice_strategy_code")String notice_strategy_code);

    /**
     * 查询选中的渠道编码和变频规则编码
     * @param notice_strategy_code
     * @return
     */
    ContactsChannel getChannelFrequency(@Param("notice_strategy_code")String notice_strategy_code);

}
