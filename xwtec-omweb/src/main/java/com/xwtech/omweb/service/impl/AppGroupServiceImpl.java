package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.AppGroupMapper;
import com.xwtech.omweb.dao.AppMapper;
import com.xwtech.omweb.model.AppCanvas;
import com.xwtech.omweb.model.AppCanvasLinks;
import com.xwtech.omweb.model.AppCanvasPosition;
import com.xwtech.omweb.model.AppGroup;
import com.xwtech.omweb.service.IAppGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by wangyu on 2017/2/16.
 */
@Service
public class AppGroupServiceImpl implements IAppGroupService {

    @Autowired
    AppGroupMapper groupMapper;

    @Autowired
    AppMapper appMapper;

    @Override
    public List<AppGroup> getAppGroups(AppGroup group, PageInfo pageInfo) {
        if (pageInfo != null) {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }

        List<AppGroup> groupList = groupMapper.getAppGroups(group);
        for (AppGroup eachGroup : groupList) {
            int appCount = 0;//appMapper.getAppsByAppGroupId(eachGroup.getId());
            eachGroup.setAppCount(appCount);
        }

        return groupList;
    }

    @Override
    public List<AppGroup> getAllAppGroups() {

        List<AppGroup> groupList = groupMapper.getAllAppGroups();
        for (AppGroup eachGroup : groupList) {
            int appCount = 0;//appMapper.getAppsByAppGroupId(eachGroup.getId());
            eachGroup.setAppCount(appCount);
        }

        return groupList;
    }

    @Override
    public AppGroup getAppGroupById(String id) {
        return groupMapper.getAppGroupById(id);
    }

    @Override
    public AppGroup getAppGroupByNum(String num) {
        return groupMapper.getAppGroupByNum(num);
    }

    @Override
    public AppGroup getAppGroupByName(String name) {
        return groupMapper.getAppGroupByName(name);
    }

    @Override
    public AppGroup getAppGroupByNumExceptSelf(AppGroup group) {
        return groupMapper.getAppGroupByNumExceptSelf(group);
    }

    @Override
    public AppGroup getAppGroupByNameExceptSelf(AppGroup group) {
        return groupMapper.getAppGroupByNameExceptSelf(group);
    }

    @Override
    public boolean createAppGroup(AppGroup group) {
        return groupMapper.createAppGroup(group) > 0;
    }

    @Override
    public boolean updateAppGroup(AppGroup group) {
        return groupMapper.updateAppGroup(group) > 0;
    }

    @Override
    public boolean deleteAppGroup(String id) {
        return groupMapper.deleteAppGroup(id) > 0;
    }

    @Override
    public boolean addAppToGroup(Map map) {
        return groupMapper.addAppToGroup(map) > 0;
    }

    @Override
    public boolean deleteAppFromGroup(String appId) {
        return groupMapper.deleteAppFromGroup(appId) > 0;
    }

    @Override
    public List<AppCanvasPosition> getAppsByGroupId(String groupId) {
        AppCanvasPosition appCanvasPosition = new AppCanvasPosition();
        appCanvasPosition.setGroupId(groupId);
        return groupMapper.getAppsByGroupId(appCanvasPosition);
    }

    @Override
    public List<AppCanvasLinks> getAppLinksByGroupId(String groupId) {
        AppCanvasLinks appCanvasLinks = new AppCanvasLinks();
        appCanvasLinks.setGroupId(groupId);
        return groupMapper.getAppLinksByGroupId(appCanvasLinks);
    }

    /**
     * 保存 拓扑图 位置  连线
     * @param appCanvas
     * @return
     */
    @Override
    @Transactional
    public boolean saveAppCanvas(AppCanvas appCanvas) {

        List<AppCanvasLinks>  appCanvasLinks =   appCanvas.getConnections();
        for (AppCanvasLinks item:appCanvasLinks){
            if(groupMapper.existAppLink(item) == 0){
                groupMapper.addAppLink(item);
            }
        }
        List<AppCanvasPosition> appCanvasPositions = appCanvas.getLocations();
        for (AppCanvasPosition item: appCanvasPositions) {
            groupMapper.updateAppCanvasPosition(item);
        }
        return true;
    }
}
