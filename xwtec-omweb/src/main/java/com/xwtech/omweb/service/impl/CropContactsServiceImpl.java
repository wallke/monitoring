package com.xwtech.omweb.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.service.AlarmService;
import com.xwtech.omweb.dao.CropContactsMapper;
import com.xwtech.omweb.model.ContactsChannel;
import com.xwtech.omweb.model.CropContacts;
import com.xwtech.omweb.model.MonitorTarget;
import com.xwtech.omweb.service.ICropContactsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
@Service("cropContactsService")
public class CropContactsServiceImpl implements ICropContactsService {

    private final static Logger logger = LoggerFactory.getLogger(CropContactsServiceImpl.class);

    @Autowired
    private CropContactsMapper cropContactsMapper;

    //ES推送
    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ElasticConfig elasticConfig;

    /**
     * 查询所有公司联系人
     *
     * @param name
     * @return
     */
    @Override
    public List<CropContacts> queryCropContacts(@Param("name")String name,@Param("email")String email,@Param("mobile")String mobile,@Param("cropName")String cropName, PageInfo pageInfo) {
       if(pageInfo !=null)
       {
           PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
       }
        List<CropContacts> cropContactsList = cropContactsMapper.queryCropContacts(name, email, mobile, cropName);
        cropContactsList.forEach(n ->{
            String type = n.getType();
            String[] split = type.split(",");
            for (String channel : split) {
                String s = cropContactsMapper.queryContactsAddr(n.getContactId(), channel);
                if (channel.equals("sms"))
                {
                    n.setMobile(s);
                }else if (channel.equals("email"))
                {
                    n.setEmail(s);
                }
            }
        });
        return cropContactsList;
    }

    /**
     * 所有联系人
     *
     * @return
     */
    @Override
    public List<CropContacts> getCropContactList() {
        List<CropContacts> cropContactsList = cropContactsMapper.queryCropContacts(null, null, null, null);
        cropContactsList.forEach(n ->{
            String type = n.getType();
            String[] split = type.split(",");
            for (String channel : split) {
                String s = cropContactsMapper.queryContactsAddr(n.getContactId(), channel);
                if (channel.equals("sms"))
                {
                    n.setMobile(s);
                }else if (channel.equals("email"))
                {
                    n.setEmail(s);
                }
            }
        });
        return cropContactsList;
    }

    /**
     * 根据公司ID查询公司下面所有联系人信息
     *
     * @param cropId
     * @return
     */
    @Override
    public List<CropContacts> queryCropContactsList(@Param("cropId") String cropId) {
        List<CropContacts> cropContactsList = cropContactsMapper.queryCropContactsList(cropId);
        cropContactsList.forEach(n ->{
            String type = n.getType();
            String[] split = type.split(",");
            for (String channel : split) {
                String s = cropContactsMapper.queryContactsAddr(n.getContactId(), channel);
                if (channel.equals("sms"))
                {
                    n.setMobile(s);
                }else if (channel.equals("email"))
                {
                    n.setEmail(s);
                }
            }
        });
        return cropContactsList;
    }


    /**
     * 新增修改ES
     * @param cropContacts
     * @return
     * @throws JsonProcessingException
     */
    @Transactional
    public int PostParamsEs(CropContacts cropContacts) throws JsonProcessingException {
        int count = 0;
        CropContacts cropContacts1 = cropContactsMapper.queryCropContactsDetail(cropContacts.getContactId());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = objectMapper.writeValueAsString(cropContacts1);
        int status = alarmService.postCommonEs(json,elasticConfig.getAccountType(),cropContacts1.getUserName());
        logger.info("es推送联系人信息,返回状态"+status+"推送数据"+json);
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
     * 新增人员信息
     *
     * @param cropContacts
     * @return
     */
    @Override
    @Transactional
    public int addCropContacts(CropContacts cropContacts) throws JsonProcessingException {
        String type = cropContacts.getType();
        String[] split = type.split(",");
        for (String channel:split) {
            ContactsChannel contactsChannel = new ContactsChannel();
            contactsChannel.setContactId(cropContacts.getContactId());
            contactsChannel.setChannelId(UUID.randomUUID().toString().replace("-",""));
            contactsChannel.setType(channel);
            if (channel.equals("sms"))
            {
                contactsChannel.setAddress(cropContacts.getMobile());
            }
            else if (channel.equals("email"))
            {
                contactsChannel.setAddress(cropContacts.getEmail());
            }
            cropContactsMapper.addContactsChannel(contactsChannel);
        }

        int count = cropContactsMapper.addCropContacts(cropContacts);
        if (count > 0)
        {
            count = PostParamsEs(cropContacts);
        }
        return count;
    }

    /**
     * 修改人员信息
     *
     * @param cropContacts
     * @return
     */
    @Override
    @Transactional
    public int updateCropContacts(CropContacts cropContacts) throws JsonProcessingException {

        //删除对照关系
        cropContactsMapper.deleteContactsChannel(cropContacts.getContactId());
        String type = cropContacts.getType();
        String[] split = type.split(",");
        for (String channel:split) {
            ContactsChannel contactsChannel = new ContactsChannel();
            contactsChannel.setContactId(cropContacts.getContactId());
            contactsChannel.setChannelId(UUID.randomUUID().toString().replace("-",""));
            contactsChannel.setType(channel);
            if (channel.equals("sms"))
            {
                contactsChannel.setAddress(cropContacts.getMobile());
            }
            else if (channel.equals("email"))
            {
                contactsChannel.setAddress(cropContacts.getEmail());
            }
            cropContactsMapper.addContactsChannel(contactsChannel);
        }

        int count = cropContactsMapper.updateCropContacts(cropContacts);

        if (count >0 )
        {
            count = PostParamsEs(cropContacts);
        }

        return count;
    }

    /**
     * 删除人员信息
     *
     * @param contactId
     * @return
     */
    @Override
    public int deleteCropContacts(String contactId,String name) {
        int count = cropContactsMapper.deleteCropContacts(contactId);
        //删除对照关系
        cropContactsMapper.deleteContactsChannel(contactId);
        if (count > 0)
        {
            count = alarmService.deleteCommonEs(elasticConfig.getAccountType(), name);
        }
        return count;
    }

    /**
     * 根据id查询人员信息详情
     *
     * @param contactId
     * @return
     */
    @Override
    public CropContacts queryCropContactsDetail(@Param("contactId") String contactId) {
        CropContacts cropContacts = cropContactsMapper.queryCropContactsDetail(contactId);
        String type = cropContacts.getType();
        String[] split = type.split(",");
        for (String channel : split) {
            String s = cropContactsMapper.queryContactsAddr(cropContacts.getContactId(), channel);
            if (channel.equals("sms"))
            {
                cropContacts.setMobile(s);
            }else if (channel.equals("email"))
            {
                cropContacts.setEmail(s);
            }
        }
        return cropContacts;
    }

    /**
     * 新增联系人应用对照关系
     *
     * @param contactId
     * @param appIdses
     * @return
     */
    @Override
    @Transactional
    public int addAppContact(@Param("contactId") String contactId,  String[] appIdses) {
        int count = 0;
        for (String appId : appIdses)
        {
             count = cropContactsMapper.addAppContact(contactId, appId);
        }
        return count;
    }

    /**
     * 删除联系人应用对照关系
     *
     * @param contactId
     * @param appId
     * @return
     */
    @Override
    public int deleteAppContact(@Param("contactId") String contactId, @Param("appId") String appId) {
        return cropContactsMapper.deleteAppContact(contactId,appId);
    }

    /**
     * 查询应用到ES数据详情
     *
     * @param contactId
     */
    @Override
    public CropContacts queryCropContactsDetailES(@Param("contactId") String contactId) {
        return cropContactsMapper.queryCropContactsDetailES(contactId);
    }

    /**
     * 根据通知ID查询联系人列表
     *
     * @param notice_strategy_code
     * @return
     */
    @Override
    public List<CropContacts> queryCropContactsByNoticeId(@Param("notice_strategy_code") String notice_strategy_code) {
        return cropContactsMapper.queryCropContactsByNoticeId(notice_strategy_code);
    }

    /**
     * 验证参数
     *
     * @param contactId
     * @param type
     * @param address
     * @return
     */
    @Override
    public int validateParam(@Param("contactId") String contactId, @Param("type") String type, @Param("address") String address) {
        return cropContactsMapper.validateParam(contactId,type,address);
    }

    /**
     * 验证用户名是否存在
     *
     * @param contactId
     * @param userName
     * @return
     */
    @Override
    public int validateUserName(@Param("contactId") String contactId, @Param("userName") String userName) {
        return cropContactsMapper.validateUserName(contactId,userName);
    }

    /**
     * 获取对照关系列表
     *
     * @return
     */
    @Override
    public List<ContactsChannel> queryContactsChannelList() {
        return cropContactsMapper.queryContactsChannelList();
    }
}
