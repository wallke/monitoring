package com.xwtech.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xwtech.admin.service.IAuthService;
import com.xwtech.framework.shiro.model.auth.Menu;
import com.xwtech.framework.shiro.model.auth.Platform;
import com.xwtech.framework.shiro.model.auth.Role;
import com.xwtech.framework.shiro.model.auth.RoleResources;
import com.xwtech.framework.web.result.JSONResult;

/**
 * 权限管理
 * Created by zl on 16/1/20.
 */
@RequestMapping("resource")
@RestController
public class ResourceController {

	final static Logger logger = LoggerFactory.getLogger(ResourceController.class);

	@Autowired
	IAuthService authService;

	/**
	 * @param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("index")
	public ModelAndView index(String platId,String roleId) {
		List<Platform> platList = authService.getPlatforms(1);
		List<Role> roleList  = authService.getRoles();
	    List<Menu> currentMenuList = new ArrayList<>();
	    RoleResources roleRes = new RoleResources();
	    List<RoleResources> roleResList = new ArrayList<RoleResources>();
	    if(StringUtils.isEmpty(platId) || StringUtils.isEmpty(roleId)){
	    	return new ModelAndView("resource/index").addObject("platList", platList).
					addObject("roleList",roleList).addObject("currentMenuList", "")
					.addObject("platId",platId).addObject("roleId",roleId);
	    }
	    if(StringUtils.isNotEmpty(platId)){
	    	roleRes.setPaltId(Long.parseLong(platId));
	    }
	    if(StringUtils.isNotEmpty(roleId)){
	    	roleRes.setRoleId(Long.parseLong(roleId));
	    	roleResList = authService.getRoleResource(roleRes);
	    }
		List<Menu> resourceList = authService.getResourcesByRoleRes(roleRes);
		
		//设置已选
		for(int i=0 ;i<roleResList.size();i++){
			RoleResources roleResources = roleResList.get(i);
			 for(int j=0;j<resourceList.size();j++){
				 Menu me = resourceList.get(j);
				 if(roleResources.getResourceId() == me.getId()){
					 me.setIsChecked(1);
				 }
			 }
		}
        if (resourceList != null) {
            for (Menu menu : resourceList) {
                if(menu.getParentId() == 0 && menu.getType().equals(Menu.ResourceType.menu)){
                    List<Menu> subs = resourceList.stream().filter(x -> x.getParentId() == menu.getId() && x.getType().equals(Menu.ResourceType.menu)).collect(Collectors.toList());
                    if(subs != null){
                        for (Menu menu1:subs){
                            List<Menu> subs1 = resourceList.stream().filter(x -> x.getParentId() == menu1.getId() && x.getType().equals(Menu.ResourceType.menu)).collect(Collectors.toList());
                            if(subs1 != null){
                                for(Menu menu2:subs1){
                                	 List<Menu> subs2 = resourceList.stream().filter(x -> x.getParentId() == menu2.getId() && x.getType().equals(Menu.ResourceType.button)).collect(Collectors.toList());
                                     if(subs2 != null){
                                    	 menu2.setSubMenus(subs2); //四级按钮
                                     }
                                }
                            	menu1.setSubMenus(subs1);//三级菜单
                            }
                        }
                        menu.setSubMenus(subs);//二级菜单
                    }
                    currentMenuList.add(menu);//一级菜单
                }

            }
        }
		return new ModelAndView("resource/index").addObject("platList", platList).
				addObject("roleList",roleList).addObject("currentMenuList", currentMenuList)
				.addObject("platId",platId).addObject("roleId",roleId);
	}

	@RequestMapping("hasRoles")
	public ModelAndView hasRole() {
		return new ModelAndView("hasRoles");
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "submit", method = RequestMethod.POST)
	@ResponseBody
	private JSONResult submit(String roleId,String [] resources) {
		JSONResult jsonResult = new JSONResult();
		logger.debug("{}", roleId);
		try {
			authService.creteRoleResources(roleId,resources);
			jsonResult.setSuccessInfo("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			jsonResult.setFailInfo("系统异常，稍后再试！");
		}
		return jsonResult;
	}

}
