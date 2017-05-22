package com.xwtech.omweb.model;

import java.util.List;

/**
 * Created by MDK on 2017-5-21.
 */
public class Select2RtnData {
    /**
     * 子对象
     */
    private List<Select2Data> children;
    /**
     *组名称
     */
    private  String text;

    public List<Select2Data> getChildren() {
        return children;
    }

    public void setChildren(List<Select2Data> children) {
        this.children = children;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
