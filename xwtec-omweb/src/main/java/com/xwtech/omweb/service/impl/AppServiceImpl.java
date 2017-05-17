package com.xwtech.omweb.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.AppMapper;
import com.xwtech.omweb.model.App;
import com.xwtech.omweb.model.AppCategory;
import com.xwtech.omweb.model.Crop;
import com.xwtech.omweb.model.CropContacts;
import com.xwtech.omweb.service.IAppService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/2/14 0014.
 */
@Service("appService")
public class AppServiceImpl implements IAppService {

    @Autowired
    private AppMapper appMapper;

    /**
     * 根据条件查询应用列表
     *
     * @param appNum
     * @param appName
     * @param categoryId
     * @param cropId
     * @return
     */
    @Override
    public List<App> queryAppList(@Param("appNum") String appNum, @Param("appName") String appName, @Param("categoryId") String categoryId, @Param("cropId") String cropId, PageInfo pageInfo) {
       if(pageInfo != null)
       {
           PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
       }
        return appMapper.queryAppList(appNum,appName,categoryId,cropId);

    }

    /**
     * 查询列表
     *
     * @return
     */
    @Override
    public List<App> getAppList() {
        return appMapper.getAppList();
    }


    /**
     * 查询未绑定的联系人应用
     *
     * @param contactId
     */
    @Override
    public List<App> queryAppListNoBindContacts(@Param("contactId") String contactId) {
        return appMapper.queryAppListNoBindContacts(contactId);
    }

    /**
     * 根据应用ID查询应用详情
     *
     * @param appId
     * @return
     */
    @Override
    public App queryAppDeatil(@Param("appId") String appId) {
        return appMapper.queryAppDeatil(appId);
    }

    /**
     * 新增App信息
     *
     * @param app
     * @return
     */
    @Override
    public int addApp(App app) {
        app.setAppId(UUID.randomUUID().toString().replace("-",""));
        return appMapper.addApp(app);
    }

    /**
     * 修改App信息
     *
     * @param app
     * @return
     */
    @Override
    public int updateApp(App app) {
        return appMapper.updateApp(app);
    }

    /**
     * 根据ID删除app应用
     *
     * @param appId
     * @return
     */
    @Override
    public int deleteAppById(@Param("appId") String appId) {
        return appMapper.deleteAppById(appId);
    }

    /**
     * 根据联系人ID查询所有应用信息列表
     *
     * @param contactId
     * @return
     */
    @Override
    public List<App> queryAllContactList(@Param("contactId") String contactId,PageInfo pageInfo) {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return appMapper.queryAllContactList(contactId);
    }

    /**
     * 根据应用组ID查询所有应用信息
     * @param groupId
     * @param pageInfo
     * @return
     */
    @Override
    public List<App> queryAppListByGroupId( @Param("groupId") String groupId, PageInfo pageInfo) {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return appMapper.queryAppListByGroupId(groupId);
    }

    /**
     * 查询所有未绑定的应用组的应用列表
     *
     * @return
     */
    @Override
    public List<App> queryAppListNotGroup() {
        return appMapper.queryAppListNotGroup();
    }

    /**
     * 查询应用名称是否存在
     *
     * @param appId
     * @return
     */
    @Override
    public int queryAppName(@Param("appName") String appName, @Param("appId") String appId) {
        return appMapper.queryAppName(appName,appId);
    }

    /**
     * 查询应用编码是否存在
     *
     * @param appId
     * @return
     */
    @Override
    public int queryAppNum(@Param("appNum") String appNum, @Param("appId") String appId) {
        return appMapper.queryAppNum(appNum,appId);
    }


}
