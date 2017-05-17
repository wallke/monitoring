package com.xwtech.omweb.enums;

/**
 * Created by Administrator on 2017/2/22 0022.
 */
public enum SdkFlag {

    Zero {
        @Override
        public  String getFlag(){
           return "0";
        }
    },
    ONE{
        @Override
        public  String getFlag(){
            return "1";
        }
    };


    public abstract String getFlag();

}
