package com.xwtech.omweb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.ContactsChannel;
import com.xwtech.omweb.model.CropContacts;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
public interface ICropContactsService {


    /**
     * 查询所有公司联系人
     * @param name
     * @return
     */
    List<CropContacts> queryCropContacts(@Param("name")String name, @Param("email")String email, @Param("mobile")String mobile, @Param("cropName")String cropName, PageInfo pageInfo);

    /**
     * 所有联系人
     * @return
     */
    List<CropContacts> getCropContactList();
    /**
     * 根据公司ID查询公司下面所有联系人信息
     * @return
     */
    List<CropContacts> queryCropContactsList(@Param("cropId")String cropId);

    /**
     * 新增人员信息
     * @param cropContacts
     * @return
     */
    int addCropContacts(CropContacts cropContacts) throws JsonProcessingException;

    /**
     * 修改人员信息
     * @param cropContacts
     * @return
     */
    int updateCropContacts(CropContacts cropContacts) throws JsonProcessingException;

    /**
     * 删除人员信息
     * @param contactId
     * @return
     */
    int deleteCropContacts(String contactId,String name);

    /**
     * 根据id查询人员信息详情
     * @param contactId
     * @return
     */
    CropContacts queryCropContactsDetail(@Param("contactId")String contactId);

    /**
     * 新增联系人应用对照关系
     * @param contactId
     * @param appIdses
     * @return
     */
    int addAppContact(@Param("contactId")String contactId, String[] appIdses);

    /**
     * 删除联系人应用对照关系
     * @param contactId
     * @param appId
     * @return
     */
    int deleteAppContact(@Param("contactId")String contactId, @Param("appId")String appId);

    /**
     * 查询应用到ES数据详情
     */
    CropContacts queryCropContactsDetailES(@Param("contactId")String contactId);

    /**
     * 根据通知ID查询联系人列表
     * @param notice_strategy_code
     * @return
     */
    List<CropContacts> queryCropContactsByNoticeId(@Param("notice_strategy_code")String notice_strategy_code);

    /**
     * 验证参数
     * @param contactId
     * @param type
     * @param address
     * @return
     */
    int validateParam(@Param("contactId")String contactId,@Param("type")String type,@Param("address")String address);

    /**
     * 验证用户名是否存在
     * @param contactId
     * @param userName
     * @return
     */
    int validateUserName(@Param("contactId")String contactId,@Param("userName")String userName);

    /**
     * 获取对照关系列表
     * @return
     */
    List<ContactsChannel> queryContactsChannelList();


}
