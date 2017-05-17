package com.xwtech.omweb.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.service.AlarmService;
import com.xwtech.omweb.dao.NoticeStrategyMapper;
import com.xwtech.omweb.model.ContactsChannel;
import com.xwtech.omweb.model.CropContacts;
import com.xwtech.omweb.model.NoticeStrategy;
import com.xwtech.omweb.service.ICropContactsService;
import com.xwtech.omweb.service.INoticeStrategyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14 0014.
 */
@Service
public class NoticeStrategyServiceImpl implements INoticeStrategyService {

    private final static Logger logger = LoggerFactory.getLogger(NoticeStrategyServiceImpl.class);

    @Autowired
    private NoticeStrategyMapper noticeStrategyMapper;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ElasticConfig elasticConfig;

    @Resource(name = "cropContactsService")
    private ICropContactsService cropContactsService;
    /**
     * 查看通知策略集合
     *
     * @return
     */
    @Override
    public List<NoticeStrategy> queryNoticeStrategyList(PageInfo pageInfo) {
        if (pageInfo !=null)
        {
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return noticeStrategyMapper.queryNoticeStrategyList();
    }

    /**
     * 查询通知策略详情
     *
     * @param notice_id
     * @return
     */
    @Override
    public NoticeStrategy queryNoticeStrategyDetail(@Param("notice_id") String notice_id) {
        return noticeStrategyMapper.queryNoticeStrategyDetail(notice_id);
    }


    @Transactional
    public int PostParamsEs(NoticeStrategy noticeStrategy) throws JsonProcessingException {
        NoticeStrategy noticeStrategy1 = this.queryNoticeStrategyDetail(noticeStrategy.getNotice_strategy_code());
        List<CropContacts> stakeholders = noticeStrategy1.getStakeholders();
        for (int i = 0; i < stakeholders.size(); i++) {
            CropContacts cropContacts = stakeholders.get(i);
            List<ContactsChannel> channelInfo = noticeStrategyMapper.getChannelInfo(cropContacts.getContactId(),noticeStrategy1.getNotice_strategy_code());
            cropContacts.setChannels(channelInfo);
        }
        int count = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = objectMapper.writeValueAsString(noticeStrategy1);
        System.out.print(json);
        int status = alarmService.postCommonEs(json,elasticConfig.getNoticeType(),noticeStrategy.getNotice_strategy_code());
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
     * 新增通知策略
     *
     * @param noticeStrategy
     * @return
     */
    @Override
    @Transactional
    public int addNoticeStrategy(NoticeStrategy noticeStrategy,String[] frequencyList,String[] channelIdLists) throws JsonProcessingException {
        //新增对照关系
        if (frequencyList !=null && frequencyList.length >0)
        {
            for (int i = 0; i < frequencyList.length; i++) {
                ContactsChannel contactsChannel = new ContactsChannel();
                contactsChannel.setFrequency_code(frequencyList[i]);
                contactsChannel.setChannelId(channelIdLists[i]);
                contactsChannel.setNotice_strategy_code(noticeStrategy.getNotice_strategy_code());
                noticeStrategyMapper.insertDzNoticeChannel(contactsChannel);
            }
        }
        int count = noticeStrategyMapper.addNoticeStrategy(noticeStrategy);
        if (count > 0)
        {
            count = PostParamsEs(noticeStrategy);
        }
        //新增通知策略
        return count;
    }

    /**
     * 修改通知策略
     *
     * @param noticeStrategy
     * @return
     */
    @Override
    @Transactional
    public int updateNoticeStrategy(NoticeStrategy noticeStrategy,String[] frequencyList,String[] channelIdLists) throws JsonProcessingException {
        noticeStrategyMapper.deleteDZNoticeChannel(noticeStrategy.getNotice_strategy_code());
        //新增对照关系
        if (frequencyList !=null && frequencyList.length >0)
        {
            for (int i = 0; i < frequencyList.length; i++) {
                ContactsChannel contactsChannel = new ContactsChannel();
                contactsChannel.setFrequency_code(frequencyList[i]);
                contactsChannel.setChannelId(channelIdLists[i]);
                contactsChannel.setNotice_strategy_code(noticeStrategy.getNotice_strategy_code());
                noticeStrategyMapper.insertDzNoticeChannel(contactsChannel);
            }
        }
        int count = noticeStrategyMapper.updateNoticeStrategy(noticeStrategy);
        if (count > 0)
        {
            count = PostParamsEs(noticeStrategy);
        }
        return count;
    }


    /**
     * 删除通知策略
     *
     * @param notice_strategy_code
     * @return
     */
    @Override
    @Transactional
    public int deleteNoticeStrategy(@Param("notice_strategy_code") String notice_strategy_code) {
        //删除对照信息
        noticeStrategyMapper.deleteDZNoticeChannel(notice_strategy_code);
        int count = noticeStrategyMapper.deleteNoticeStrategy(notice_strategy_code);
        if (count > 0)
        {
            //删除es数据
            count = alarmService.deleteCommonEs(elasticConfig.getNoticeType(),notice_strategy_code);
        }
        return count;
    }

    /**
     * 查询选中的渠道编码和变频规则编码
     *
     * @param notice_strategy_code
     * @return
     */
    @Override
    public ContactsChannel getChannelFrequency(@Param("notice_strategy_code") String notice_strategy_code) {
        return noticeStrategyMapper.getChannelFrequency(notice_strategy_code);
    }
}
