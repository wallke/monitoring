package com.xwtech.admin.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.admin.service.IAuthService;
import com.xwtech.admin.service.PasswordHelper;
import com.xwtech.framework.shiro.model.auth.Role;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.shiro.model.auth.UserRole;
import com.xwtech.framework.web.result.JSONResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import xwtec.servercm.ServerCM;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zq on 16/9/9.
 */
@RequestMapping("user")
@RestController
public class UserController {

	final static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	IAuthService authService;

	@Autowired
	PasswordHelper passwordHelper;

	@Autowired
	RestTemplate restTemplate;

	/**
	 *
	 *
	 * @param
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	@RequestMapping("index")
	public ModelAndView index(HttpServletRequest request,
	        String name, String loginName, @RequestParam(name = "ps", defaultValue = "10") int ps,
			@RequestParam(name = "pn", defaultValue = "1") int pn) {

        ServerCM.startRequest("user_index","IOS",request.getRemoteHost(),String.valueOf(request.getRemotePort()) );

		PageInfo<User> pageInfo = new PageInfo<User>();
		pageInfo.setPageSize(ps < 10 ? ps : 10);
		pageInfo.setPageNum(pn);

		User user = new User();
		user.setLoginName(loginName);
		user.setName(name);
		//ServerCM.request("198.168.2.1","8080","check");
		//remote
		//ServerCM.response("200","");

        //ServerCM.httpRequestParam()

        logger.debug("client:createSession:{}", "xxxsss");
//TODO 埋点测试
        Map param = new HashMap();
        param.put("appKey", "xxxsss");
        param.put("cm", ServerCM.httpRequestParam());
        MultiValueMap<String, Object> headers;
        headers = new LinkedMultiValueMap<>();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        HttpEntity<Map> request1 = new HttpEntity(param, headers);

        try {
            ServerCM.request("app_xwtec_jobs","");
            ResponseEntity<Map> responseEntity =
                    restTemplate.postForEntity( "https://127.0.0.1:8090/get1", request1, Map.class);

            Object result = responseEntity.getBody().get("result");

            ServerCM.response(String.valueOf(responseEntity.getStatusCodeValue()));

        } catch (Exception ex) {
            logger.error("client:createSession 创建Session 请求异常 ", ex);

        }

		List<User> list = authService.getUsers(user, pageInfo);

        ServerCM.exit();
		return new ModelAndView("user/index").addObject("userList", list).addObject("user", user).addObject("pageInfo",
				((Page<User>) list).toPageInfo());
	}

	@RequestMapping("hasRoles")
	// @RequiresRoles("superamdin1")
	public ModelAndView hasRole() {
		return new ModelAndView("hasRoles");
	}

	@RequestMapping("/password")
	public ModelAndView password() {
		return new ModelAndView("user/password");
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "password", method = RequestMethod.POST)
	@ResponseBody
	public JSONResult passwordSubmit(String password, String newPassword, String confirmPassword) {
		JSONResult jsonResult = new JSONResult();
		if (StringUtils.isEmpty(password)) {
			jsonResult.setFailInfo("原密码不能为空！");
			return jsonResult;
		}
		if (StringUtils.isEmpty(newPassword)) {
			jsonResult.setFailInfo("新密码不能为空！");
			return jsonResult;
		}
		if (!newPassword.equals(confirmPassword)) {
			jsonResult.setFailInfo("两次密码输入不一致！");
			return jsonResult;
		}

		User user = authService.getUserByLoginName(SecurityUtils.getSubject().getPrincipal().toString());

		User u = new User();
		u.setPassword(password);
		u.setSalt(user.getSalt());
		u.setLoginName(user.getLoginName());
		passwordHelper.encryptPassword(u);

		if (!u.getPassword().equals(user.getPassword())) {
			jsonResult.setFailInfo("原密码不正确！");
			return jsonResult;
		}
		if (authService.changePassword(user.getId(), newPassword)) {

		} else {
			jsonResult.setFailInfo("密码修改失败！");
		}
		return jsonResult;
	}

	@RequestMapping(value = "create", method = RequestMethod.GET)
	private ModelAndView create(String userId) {

	User user = new User();
	String actionType = "create";
	List<UserRole> userRoleList = new ArrayList<UserRole>();

	//查询所有角色
	List<Role> roleList = authService.getRoles();

	if (StringUtils.isNotEmpty(userId)) {
		user = authService.getUerById(Long.valueOf(userId));
		actionType= "modfiy";
		//查询用户角色
		userRoleList = authService.getUserRoleByUserId(Long.valueOf(userId));
	}

	//用户存在的角色，设置已选，复用avaiable字段
	if(userRoleList!=null && userRoleList.size()>0){
		for(int i= 0 ;i < userRoleList.size();i++ ){
			UserRole userRole = userRoleList.get(i);
			for(int j= 0 ;j < roleList.size();j++ ){
				Role role = roleList.get(j);
				if(String.valueOf(userRole.getRoleId()).equals(String.valueOf(role.getId()))){
					role.setAvailable(3);
				}
			}
		}
	}

	return new ModelAndView("user/create").addObject("user", user).
	addObject("actionType", actionType).addObject("roleList", roleList);
}

	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	private JSONResult createSubmit(String name, String loginName, String locked,String actionType,
			String roles) {
		JSONResult jsonResult = new JSONResult();
		try {
			User user = authService.getUserByLoginName(loginName);
			if("create".equals(actionType)){
				if (user!=null && StringUtils.isNotEmpty(user.getLoginName())) {
					jsonResult.setFailInfo("登录名已存在，无法新增！");
					return jsonResult;
				}
				user = new User();
				user.setName(name);
				user.setLoginName(loginName);
				user.setPassword("000000");
				Date currentTime = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				user.setCreateTime(formatter.format(currentTime));
				if (StringUtils.isEmpty(locked)) {
					user.setLocked(1);
				} else {
					user.setLocked(0);
				}
				logger.debug("{}", user);
				authService.createUser(user,roles);
				jsonResult.setSuccessInfo("新增用户成功！");
			}else if("modfiy".equals(actionType)){
				if (user == null) {
					jsonResult.setFailInfo("用户已被删除，无法修改！");
					return jsonResult;
				}
				user.setName(name);
				if (StringUtils.isEmpty(locked)) {
					user.setLocked(1);
				} else {
					user.setLocked(0);
				}
				authService.updateUser(user,roles);
				jsonResult.setSuccessInfo("修改用户成功！");
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
			authService.deleteUser(Long.valueOf(id));
			jsonResult.setSuccessInfo("删除用户成功！");
		} catch (Exception e) {
			e.printStackTrace();
			jsonResult.setSuccessInfo("系统异常，稍后再试！");
		}
		return jsonResult;
	}

}
