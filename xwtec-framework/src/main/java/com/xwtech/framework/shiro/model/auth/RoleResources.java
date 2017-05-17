package com.xwtech.framework.shiro.model.auth;

import java.io.Serializable;

/**
 * Created by zl on 17/1/19.
 */
public class RoleResources implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 角色ID
	 */
	long roleId;  
    
	/**
	 * 菜单ID
	 */
    long resourceId;
    
    /**
     * 平台ID
     * @return
     */
    long paltId;

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public long getPaltId() {
		return paltId;
	}

	public void setPaltId(long paltId) {
		this.paltId = paltId;
	}

}
