package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.App;
import com.xwtech.omweb.model.AppCategory;
import com.xwtech.omweb.model.Crop;
import com.xwtech.omweb.model.CropContacts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/2/14 0014.
 */
@Mapper
public interface AppMapper {

    /**
     * 根据条件查询应用列表
     * @param appNum
     * @param appName
     * @param categoryId
     * @return
     */
     List<App> queryAppList(@Param("appNum")String appNum, @Param("appName")String appName, @Param("categoryId")String categoryId, @Param("cropId")String cropId);

    /**
     *查询列表
     * @return
     */
     List<App> getAppList();

    /**
     * 根据应用ID查询应用详情
     * @param appId
     * @return
     */
     App queryAppDeatil(@Param("appId")String appId);

    /**
     * 新增App信息
     * @param app
     * @return
     */
     int addApp(App app);

    /**
     * 修改App信息
     * @param app
     * @return
     */
     int updateApp(App app);

    /**
     * 根据ID删除app应用
     * @return
     */
    int deleteAppById(@Param("appId")String appId);

    /**
     * 根据联系人ID查询所有应用信息列表
     * @param contactId
     * @return
     */
    List<App> queryAllContactList(@Param("contactId")String contactId);

    /**
     * 查询未绑定的联系人应用
     */
    List<App> queryAppListNoBindContacts(@Param("contactId")String contactId);

    /**
     * 根据应用组ID查询所有应用信息
     * @param groupId
     * @return
     */
    List<App> queryAppListByGroupId(@Param("groupId")String groupId);

    /**
     * 查询所有未绑定的应用组的应用列表
     * @return
     */
    List<App> queryAppListNotGroup();

    /**
     * 查询应用名称是否存在
     * @return
     */
    int queryAppName(@Param("appName")String appName,@Param("appId")String appId);

    /**
     * 查询应用编码是否存在
     * @return
     */
    int queryAppNum(@Param("appNum")String appNum,@Param("appId")String appId);



}
