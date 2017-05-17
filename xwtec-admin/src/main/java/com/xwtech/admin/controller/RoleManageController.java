package com.xwtech.admin.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.admin.service.IAuthService;
import com.xwtech.framework.shiro.model.auth.Platform;
import com.xwtech.framework.shiro.model.auth.Role;
import com.xwtech.framework.shiro.model.auth.UserRole;
import com.xwtech.framework.web.result.JSONResult;

/**
 * 角色管理
 * Created by zl on 17/1/18.
 */
@RequestMapping("role")
@RestController
public class RoleManageController {

	final static Logger logger = LoggerFactory.getLogger(RoleManageController.class);

	@Autowired
	IAuthService authService;

	/**
	 *
	 *角色查询
	 * @param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("index")
	public ModelAndView index(String name, String code, @RequestParam(name = "ps", defaultValue = "10") int ps,
			@RequestParam(name = "pn", defaultValue = "1") int pn) {

		PageInfo<Role> pageInfo = new PageInfo<Role>();
		pageInfo.setPageSize(ps < 10 ? ps : 10);
		pageInfo.setPageNum(pn);

		Role role= new Role();
		role.setName(name);
		role.setCode(code);
		List<Role> list = authService.getRoles(role, pageInfo);
		List<Platform> platList = authService.getPlatforms(1);
		String platId = "1";
		if(platList!=null && platList.size()>0){
			Platform plat = platList.get(0);
			platId = String.valueOf(plat.getId());
		}

		return new ModelAndView("role/roleindex").addObject("roleList", list).addObject("role", role).addObject("pageInfo",
				((Page<Role>) list).toPageInfo()).addObject("platId", platId);
	}

	@RequestMapping("hasRoles")
	// @RequiresRoles("superamdin1")
	public ModelAndView hasRole() {
		return new ModelAndView("hasRoles");
	}
	
	/**
	 * 角色新增跳转
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "create", method = RequestMethod.GET)
	private ModelAndView create(String id) {

		Role role = new Role();
		String actionType = "create";
		if (StringUtils.isNotEmpty(id)) {
			role = authService.getRoleById(Long.parseLong(id));
			actionType= "modfiy";
		}
		return new ModelAndView("role/create").addObject("role", role).addObject("actionType", actionType);
	}
	
	/**
	 * 角色新增修改
	 * @param name
	 * @param code
	 * @param available
	 * @param actionType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	private JSONResult createSubmit(String name, String code, String available,String actionType,
			String id,String description) {
		JSONResult jsonResult = new JSONResult();
		
		Role role = new Role();
		if (!"null".equals(available)) {
			role.setAvailable(1);
		} else {
			role.setAvailable(0);
		}
		role.setName(name);
		role.setCode(code);
		role.setDescription(description);
		try {
			
			if("create".equals(actionType)){
				Role roleName = authService.getRoleByName(name);
				if (roleName!=null) {
					jsonResult.setFailInfo("角色名称已经存在，无法新增！");
					return jsonResult;
				}
				
				Role roleCode = authService.getRoleByCode(code);
				if (roleCode!=null) {
					jsonResult.setFailInfo("角色编码已经存在，无法新增！");
					return jsonResult;
				}
				
				authService.createRole(role);
				jsonResult.setSuccessInfo("新增角色成功！");
			}else if("modfiy".equals(actionType)){
				role.setId(Long.parseLong(id));
				Role roleInfo = authService.getRoleById(Long.parseLong(id));
				
				if(roleInfo==null){
					jsonResult.setFailInfo("角色已被删除，无法修改！");
					return jsonResult;
				}
				
			    Role roleName = authService.getRoleByName(name);
				if (roleName!=null && !id.equals(String.valueOf(roleName.getId()))) {
					jsonResult.setFailInfo("角色名称已经存在，无法修改！");
					return jsonResult;
				}
				
				authService.updateRole(role);
				jsonResult.setSuccessInfo("修改角色成功！");
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			jsonResult.setFailInfo("系统异常，请稍后再试！");
			return jsonResult;
		}
		return jsonResult;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	private JSONResult deleteSubmit(String id) {
		JSONResult jsonResult = new JSONResult();
		logger.debug("{}", id);
		try {
			UserRole userRole = new UserRole();
			userRole.setRoleId(Long.parseLong(id));
			authService.deleteUserRole(userRole);
			jsonResult.setSuccessInfo("删除角色成功！");
		} catch (Exception e) {
			e.printStackTrace();
			jsonResult.setFailInfo("系统异常，稍后再试！");
		}
		return jsonResult;
	}
	
}
