package com.xwtech.framework.shiro.model.auth;

import java.io.Serializable;

/**
 * Created by zhangq on 2017/1/12.
 */
public class Platform  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2808495255520858775L;
	

	Long id;

    String name;

    String code;

    String createTime;

    String description;

    int available;

    String url;

    boolean current;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }
}
