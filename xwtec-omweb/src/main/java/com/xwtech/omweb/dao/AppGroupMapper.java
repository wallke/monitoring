package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.AppCanvasLinks;
import com.xwtech.omweb.model.AppCanvasPosition;
import com.xwtech.omweb.model.AppGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangyu on 2017/2/16.
 */
public interface AppGroupMapper {

    List<AppGroup> getAppGroups(AppGroup group);
    List<AppGroup> getAllAppGroups();

    AppGroup getAppGroupById(String id);
    AppGroup getAppGroupByNum(String num);
    AppGroup getAppGroupByName(String name);
    AppGroup getAppGroupByNumExceptSelf(AppGroup group);
    AppGroup getAppGroupByNameExceptSelf(AppGroup group);

    int createAppGroup(AppGroup group);
    int updateAppGroup(AppGroup group);
    int deleteAppGroup(String id);

    int addAppToGroup(Map map);
    int deleteAppFromGroup(String appId);



    List<AppCanvasPosition> getAppsByGroupId(AppCanvasPosition appCanvasPosition);


    List<AppCanvasLinks>  getAppLinksByGroupId(AppCanvasLinks appCanvasLinks);


    int updateAppCanvasPosition(AppCanvasPosition appCanvasPosition);

    int existAppLink(AppCanvasLinks appCanvasLinks);

    int addAppLink(AppCanvasLinks appCanvasLinks);

}
