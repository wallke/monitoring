package com.xwtech.es.model;

/**
 * Created by zhangq on 2017/3/10.
 */
public enum DateEnum {

    // 利用构造函数传参
    Custom(0),
    OneHours(1),
    SixHours(2),
    TwelveHours(3),
    OneDays(4),
    SevenDays(5),
    FourteenDays(6);

    // 定义私有变量
    private int code;

    // 构造函数，枚举类型只能为私有
    DateEnum(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }


}
