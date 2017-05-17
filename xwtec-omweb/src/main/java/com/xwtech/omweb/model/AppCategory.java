package com.xwtech.omweb.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/14 0014.
 * APP分类信息
 */
public class AppCategory {

    //分类信息主键
    private String categoryId;
    //父节点ID
    private int parentId;
    //分类名称
    private String categoryName;
    //是否继承父类属性(0-否;1-是;)
    private int isInherit;
    //级别编码
    private String JBNum;
    //级别
    private int JB;
    //是否末级(0-否;1-是;)
    private int MJ;
    //是否被逻辑删除（1-否;0-是）
    private String isUse;
    //是否启用（0-否;1-是）
    private String isEnabled;
    //说明
    private String desc;
    //备注
    private String memo;
    //创建人编码
    private String createManCode;
    //创建人姓名
    private String createManName;
    //创建时间
    private String createTime;
    //创建备注
    private String createMemo;
    //最后操作人编码
    private String lastManCode;
    //最后操作人名称
    private String lastManName;
    //最后操作时间
    private String lastTime;
    //最后操作备注
    private String lastMemo;

    List<AppCategory> subAppCategory ;


    @Override
    public String toString() {
        return "AppCategory{" +
                "categoryId='" + categoryId + '\'' +
                ", parentId=" + parentId +
                ", categoryName='" + categoryName + '\'' +
                ", isInherit=" + isInherit +
                ", JBNum='" + JBNum + '\'' +
                ", JB=" + JB +
                ", MJ=" + MJ +
                ", isUse='" + isUse + '\'' +
                ", isEnabled='" + isEnabled + '\'' +
                ", desc='" + desc + '\'' +
                ", memo='" + memo + '\'' +
                ", createManCode='" + createManCode + '\'' +
                ", createManName='" + createManName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", createMemo='" + createMemo + '\'' +
                ", lastManCode='" + lastManCode + '\'' +
                ", lastManName='" + lastManName + '\'' +
                ", lastTime='" + lastTime + '\'' +
                ", lastMemo='" + lastMemo + '\'' +
                ", subAppCategory=" + subAppCategory +
                '}';
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<AppCategory> getSubAppCategory() {
        return subAppCategory;
    }

    public void setSubAppCategory(List<AppCategory> subAppCategory) {
        this.subAppCategory = subAppCategory;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getIsInherit() {
        return isInherit;
    }

    public void setIsInherit(int isInherit) {
        this.isInherit = isInherit;
    }

    public String getJBNum() {
        return JBNum;
    }

    public void setJBNum(String JBNum) {
        this.JBNum = JBNum;
    }

    public int getJB() {
        return JB;
    }

    public void setJB(int JB) {
        this.JB = JB;
    }

    public int getMJ() {
        return MJ;
    }

    public void setMJ(int MJ) {
        this.MJ = MJ;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCreateManCode() {
        return createManCode;
    }

    public void setCreateManCode(String createManCode) {
        this.createManCode = createManCode;
    }

    public String getCreateManName() {
        return createManName;
    }

    public void setCreateManName(String createManName) {
        this.createManName = createManName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateMemo() {
        return createMemo;
    }

    public void setCreateMemo(String createMemo) {
        this.createMemo = createMemo;
    }

    public String getLastManCode() {
        return lastManCode;
    }

    public void setLastManCode(String lastManCode) {
        this.lastManCode = lastManCode;
    }

    public String getLastManName() {
        return lastManName;
    }

    public void setLastManName(String lastManName) {
        this.lastManName = lastManName;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastMemo() {
        return lastMemo;
    }

    public void setLastMemo(String lastMemo) {
        this.lastMemo = lastMemo;
    }
}
