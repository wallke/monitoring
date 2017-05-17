package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.ContactsChannel;
import com.xwtech.omweb.model.CropContacts;
import com.xwtech.omweb.model.NoticeStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/3/14 0014.
 */
@Mapper
public interface NoticeStrategyMapper {

    /**
     * 查看通知策略集合
     * @return
     */
    List<NoticeStrategy> queryNoticeStrategyList();

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
    int addNoticeStrategy(NoticeStrategy noticeStrategy);

    /**
     * 修改通知策略
     * @param noticeStrategy
     * @return
     */
    int updateNoticeStrategy(NoticeStrategy noticeStrategy);

    /**
     * 删除通知策略
     * @param notice_strategy_code
     * @return
     */
    int deleteNoticeStrategy(@Param("notice_strategy_code")String notice_strategy_code);

    /**
     * 修改对照关系
     * @param contactsChannel
     * @return
     */
    int insertDzNoticeChannel(ContactsChannel contactsChannel);

    /**
     * 根据通知策略编码删除对照关系
     * @param notice_strategy_code
     * @return
     */
    int deleteDZNoticeChannel(@Param("notice_strategy_code")String notice_strategy_code);

    /**
     * 查询选中的渠道编码和变频规则编码
     * @param notice_strategy_code
     * @return
     */
    ContactsChannel getChannelFrequency(@Param("notice_strategy_code")String notice_strategy_code);

    /**
     * 关联联系人查询
     */
    List<CropContacts> getUserNameES(@Param("notice_strategy_code")String notice_strategy_code);

    List<ContactsChannel> getChannelInfo(@Param("contactId")String contactId,@Param("notice_strategy_code")String notice_strategy_code);




}
