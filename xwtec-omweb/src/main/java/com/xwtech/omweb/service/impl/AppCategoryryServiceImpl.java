package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.AppCategoryMapper;
import com.xwtech.omweb.model.AppCategory;
import com.xwtech.omweb.service.IAppCategoryService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
@Service("appCategoryService")
public class AppCategoryryServiceImpl implements IAppCategoryService {
    @Autowired
    private AppCategoryMapper appCategoryMapper;

    /**
     * 查询所有应用类别
     *
     * @param JBNum
     * @return
     */
    @Override
    public List<AppCategory> queryCategoryList(@Param("JBNum") String JBNum,@Param("JB")String JB) {
        return appCategoryMapper.queryCategoryList(JBNum,JB);
    }

    @Override
    public List<AppCategory> getCategoryList() {
        return appCategoryMapper.getCategoryList();
    }

    /**
     * 查询下一级别编码
     *
     * @param JBNum
     * @return
     */
    @Override
    public List<AppCategory> queryChildNode(@Param("JBNum") String JBNum,@Param("JB")int JB) {
        return appCategoryMapper.queryChildNode(JBNum,JB);
    }

    /**
     * 新增应用类别
     *
     * @param appCategory
     * @return
     */
    @Override
    public int addAppCategory(AppCategory appCategory) {
        return appCategoryMapper.addAppCategory(appCategory);
    }

    /**
     * 修改应用类别
     *
     * @param appCategory
     * @return
     */
    @Override
    public int updateAppCategory(AppCategory appCategory) {
        return appCategoryMapper.updateAppCategory(appCategory);
    }

    /**
     * 删除应用类别
     *
     * @param categoryId
     * @return
     */
    @Override
    public int deleteAppCategory(@Param("categoryId") String categoryId) {
        return appCategoryMapper.deleteAppCategory(categoryId);
    }

    /**
     * 查询应用类别详情
     *
     * @param categoryId
     * @return
     */
    @Override
    public AppCategory queryAppCategoryDetail(@Param("categoryId") String categoryId) {
        return appCategoryMapper.queryAppCategoryDetail(categoryId);
    }

    /**
     * 根据应用类别Id查询下面是否有节点
     *
     * @param categoryId
     * @return
     */
    @Override
    public int queryAppCategoryCount(@Param("categoryId") String categoryId) {
        return appCategoryMapper.queryAppCategoryCount(categoryId);
    }

}
