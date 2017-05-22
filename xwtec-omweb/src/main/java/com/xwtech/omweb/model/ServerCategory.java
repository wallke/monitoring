package com.xwtech.omweb.model;

/**
 * Created by mdk on 2017-5-18.
 * 主机分类
 */
public class ServerCategory {


    public String serverCategoryId;// F_SERVER_CATEGORY_ID;分类ID
    public String serverCategoryName;// F_SERVER_CATEGORY_NAME;分类名字
    public String identification;// F_IDENTIFICATION;标识,默认为1
    public String bigIco;//F_BIG_ICO;大图标存放地址
    public String smallIco;//F_SMALL_ICO;小图标存放地址
    public String  mem;//F_MEM;备注

    public String getServerCategoryId() {
        return serverCategoryId;
    }

    public void setServerCategoryId(String serverCategoryId) {
        this.serverCategoryId = serverCategoryId;
    }



    public String getServerCategoryName() {
        return serverCategoryName;
    }

    public void setServerCategoryName(String serverCategoryName) {
        this.serverCategoryName = serverCategoryName;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getBigIco() {
        return bigIco;
    }

    public void setBigIco(String bigIco) {
        this.bigIco = bigIco;
    }

    public String getSmallIco() {
        return smallIco;
    }

    public void setSmallIco(String smallIco) {
        this.smallIco = smallIco;
    }

    public String getMem() {
        return mem;
    }

    public void setMem(String mem) {
        this.mem = mem;
    }


}
