package com.xwtech.omweb.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.AppCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
public interface IAppCategoryService {
    /**
     * 查询所有应用类别
     * @return
     */
    List<AppCategory> queryCategoryList(@Param("JBNum")String JBNum,@Param("JB")String JB);

    List<AppCategory> getCategoryList();

    /**
     * 查询下一级别编码
     * @param JBNum
     * @return
     */
    List<AppCategory> queryChildNode(@Param("JBNum")String JBNum,@Param("JB")int JB);

    /**
     * 新增应用类别
     * @param appCategory
     * @return
     */
    int addAppCategory(AppCategory appCategory);

    /**
     * 修改应用类别
     * @param appCategory
     * @return
     */
    int updateAppCategory(AppCategory appCategory);

    /**
     * 删除应用类别
     * @param categoryId
     * @return
     */
    int deleteAppCategory(@Param("categoryId")String categoryId);

    /**
     * 查询应用类别详情
     * @param categoryId
     * @return
     */
    AppCategory queryAppCategoryDetail(@Param("categoryId")String categoryId);

    /**
     * 根据应用类别Id查询下面是否有节点
     * @param categoryId
     * @return
     */
    int queryAppCategoryCount(@Param("categoryId")String categoryId);
}
