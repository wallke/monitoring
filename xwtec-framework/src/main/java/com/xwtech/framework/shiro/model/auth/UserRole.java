package com.xwtech.framework.shiro.model.auth;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by zl on 17/1/19.
 */
public class UserRole implements Serializable {

    long userId;

    long roleId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

}
