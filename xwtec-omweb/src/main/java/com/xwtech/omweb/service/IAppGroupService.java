package com.xwtech.omweb.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.AppCanvas;
import com.xwtech.omweb.model.AppCanvasLinks;
import com.xwtech.omweb.model.AppCanvasPosition;
import com.xwtech.omweb.model.AppGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by wangyu on 2017/2/16.
 */
public interface IAppGroupService {

    List<AppGroup> getAppGroups(AppGroup group, PageInfo pageInfo);
    List<AppGroup> getAllAppGroups();

    AppGroup getAppGroupById(String id);
    AppGroup getAppGroupByNum(String num);
    AppGroup getAppGroupByName(String name);
    AppGroup getAppGroupByNumExceptSelf(AppGroup group);
    AppGroup getAppGroupByNameExceptSelf(AppGroup group);

    boolean createAppGroup(AppGroup group);
    boolean updateAppGroup(AppGroup group);
    boolean deleteAppGroup(String id);

    boolean addAppToGroup(Map map);
    boolean deleteAppFromGroup(String appId);




    List<AppCanvasPosition> getAppsByGroupId(String groupId);


    List<AppCanvasLinks>  getAppLinksByGroupId(String groupId);


    boolean saveAppCanvas(AppCanvas appCanvas);


}
