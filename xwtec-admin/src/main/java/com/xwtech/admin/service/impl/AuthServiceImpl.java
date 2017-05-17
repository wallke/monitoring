package com.xwtech.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.admin.dao.AuthMapper;
import com.xwtech.admin.service.IAuthService;
import com.xwtech.admin.service.PasswordHelper;
import com.xwtech.framework.shiro.model.auth.*;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xwtec.servercm.ServerCM;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zq on 16/8/25.
 */
@Service("authService")
public class AuthServiceImpl implements IAuthService {


    public static final String CENTER_AUTH_CACHE = "center::auth";


    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private PasswordHelper passwordHelper;

    @Autowired
    BeanFactoryCacheOperationSourceAdvisor waitForCachingAspect;


    @Override
    public List<Platform> getPlatforms(int available){
       return authMapper.getPlatforms(null).stream().filter(e->e.getAvailable() == 1).collect(Collectors.toList());
    }


    @Override
    public Platform getPlatform(String code){
        List<Platform>  list =  authMapper.getPlatforms(null)
                .stream()
                .filter(e->e.getCode().equals(code))
                .collect(Collectors.toList());
        if(list != null && list.size() == 1){
            return list.get(0);
        }
        return null;
    }


    @Override
    public App createApp(App app) {
        authMapper.insertApp(app);
        return app;
    }

    @Override
    public boolean updateApp(App app) {
        return authMapper.updateAppById(app) > 0;
    }

    @Override
    public boolean deleteApp(Long appId) {
        return authMapper.deleteAppById(appId) > 0;
    }

    @Override
    public App getApp(Long appId) {
        return authMapper.getAppById(appId);
    }

    @Override
    public List<App> getApps() {
        return authMapper.getApps(null);
    }


    @Override
    public List getApps(App app, PageInfo pageInfo) {
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        return authMapper.getApps(app);
    }

    @Override
    public Long getAppIdByCode(String code) {
        return authMapper.getAppIdByCode(code);
    }

    @Override
    public App getAppByCode(String appKey) {
        return authMapper.getAppByCode(appKey);
    }


    @Override
    public Menu createResource(Menu resource) {
        authMapper.insertResource(resource);
        return resource;
    }

    @Override
    public boolean updateResource(Menu resource) {
        return authMapper.updateResourceById(resource) > 0;
    }

    @Override
    public boolean deleteResource(Long resourceId) {
        return authMapper.deleteResourceById(resourceId) > 0;
    }

    @Override
    public Menu getResourceById(Long resourceId) {
        return authMapper.getResourceById(resourceId);

    }

    @Override
    public List<Menu> getResources(Menu resource) {
        return authMapper.getResources(resource);
    }
    
    @Override
    public List<Menu> getResourcesByRoleRes(RoleResources roleRes){
    	return authMapper.getResourcesByRoleRes(roleRes);
    }
    
    @Override
    public List<RoleResources> getRoleResource(RoleResources roleRes){
    	return authMapper.getRoleResource(roleRes);
    }

    @Override
    public List<Menu> getResourcesByUserId(long userId) {
        return authMapper.getResourcesByUserId(userId);
    }


    @Override
    public List<Menu> getMenus(List<String> permissions) {
        List<Menu> allResources = getResources(null);
        List<Menu> menus = new ArrayList<Menu>();
        for (Menu resource : allResources) {
//            if (resource.getParentId() == 0) {
//                continue;
//            }
            if (resource.getType() != Menu.ResourceType.menu) {
                continue;
            }
            if (!hasPermission(permissions, resource)) {
                continue;
            }
            menus.add(resource);
        }
        return menus;
    }


    private boolean hasPermission(List<String> permissions, Menu resource) {
        if (StringUtils.isEmpty(resource.getPermission())) {
            return true;
        }
        for (String permission : permissions) {
            WildcardPermission p1 = new WildcardPermission(permission);
            WildcardPermission p2 = new WildcardPermission(resource.getPermission());
            if (p1.implies(p2) || p2.implies(p1)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Role createRole(Role role) {
        authMapper.insertRole(role);
        return role;
    }

    @Override
    public boolean updateRole(Role role) {
        return authMapper.updateRoleById(role) > 0;
    }

    @Override
    public boolean deleteRole(Long roleId) {
        return authMapper.deleteRoleById(roleId) > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteUserRole(UserRole userRole){
    	 //删除角色
    	 authMapper.deleteRoleById(userRole.getRoleId());
    	 //删除本角色的人员对照关系
    	 authMapper.deleteUserRole(userRole);
    	//删除本角色的权限对照关系
    	 authMapper.deleteRoleResourceById(userRole.getRoleId());
    	 return  true;
    }

    @Override
    public Role getRoleById(Long roleId) {
        return authMapper.getRoleById(roleId);
    }
    
    @Override
    public Role getRoleByName(String name){
    	return authMapper.getRoleByName(name);
    }
    
    @Override
    public Role getRoleByCode(String code){
    	return authMapper.getRoleByCode(code);
    }

    @Override
    public List<Role> getRoles() {
        return authMapper.getRoles(null);
    }
    
    @Override
    public List<UserRole> getUserRoleByUserId(Long userId){
    	return authMapper.getUserRoleByUserId(userId);
    }

    @Override
    public List<Role> getRoles(Role role, PageInfo pageInfo) {
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());

        return authMapper.getRoles(role);
    }

    @Override
    public List<String> getRoles(Long... roleIds) {
        List<String> roles = new ArrayList<>();
        for (Long roleId : roleIds) {
            Role role = getRoleById(roleId);
            if (role != null) {
                roles.add(role.getName());
            }
        }
        return roles;
    }


    @Override
    @Cacheable(cacheNames = CENTER_AUTH_CACHE, key = "#root.methodName + '_' + #loginName")
    public Set<String> getPermissions(String loginName) {
        ServerCM.startLocal("getPermissions");
        if (StringUtils.isEmpty(loginName)) {
            return Collections.EMPTY_SET;
        }
        User user = getUserByLoginName(loginName);
        if (user == null) {
            return Collections.EMPTY_SET;
        }

        List<Menu> resources = getResourcesByUserId(user.getId());

        if (resources == null || resources.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<String> stringSet = new HashSet<>();
        for (Menu resource : resources) {
            if (resource.getAvailable() == 0) {
                continue;
            }
            stringSet.add(resource.getPermission());
        }
        ServerCM.exit();
        return stringSet;
    }


    @Override
    @Cacheable(cacheNames = CENTER_AUTH_CACHE, key = "#root.methodName + '_' + #loginName")
    public Set<String> getRoles(String loginName) {
        ServerCM.startLocal("getRoles");
        if (StringUtils.isEmpty(loginName)) {
            return Collections.EMPTY_SET;
        }
        User user = getUserByLoginName(loginName);
        if (user == null) {
            return Collections.EMPTY_SET;
        }
        List<Role> roles = authMapper.getRolesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<String> stringSet = new HashSet<>();
        for (Role role : roles) {
            if (role.getAvailable() == 0) {
                continue;
            }
            stringSet.add(role.getCode());
        }
        ServerCM.exit();
        return stringSet;
    }

    @Override
    public User createUser(User user) {
        passwordHelper.encryptPassword(user);
        authMapper.insertUser(user);
        return user;
    }
    
    @Override
    @Transactional
    public User createUser(User user,String roles) {
    	this.createUser(user);
        String[] role = roles.split(",");
        if(role.length>0){
        	 User newUser = authMapper.getUserByLoginName(user.getLoginName());
             UserRole userRole = new UserRole();
             userRole.setUserId(newUser.getId());
             
             for(int i = 0 ;i < role.length;i++ ){
            	 userRole.setRoleId(Long.parseLong(role[i]));
            	 authMapper.insertUserRole(userRole);
             }
        }
    	return user;
    }

    @Override
    public boolean updateUser(User user) {
        return authMapper.updateUserById(user) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateUser(User user,String roles) {
    	this.updateUser(user);
    	 UserRole userRole = new UserRole();
    	 userRole.setUserId(user.getId());
    	 authMapper.deleteUserRole(userRole);
    	 String[] role = roles.split(",");
         if(role.length>0){
         	 User newUser = authMapper.getUserByLoginName(user.getLoginName());
              userRole = new UserRole();
              
              userRole.setUserId(newUser.getId());
              for(int i = 0 ;i < role.length;i++ ){
             	 userRole.setRoleId(Long.parseLong(role[i]));
             	 authMapper.insertUserRole(userRole);
              }
         }
        return true;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
    	UserRole userRole = new UserRole();
    	userRole.setUserId(userId);
    	//删除用户和角色对照关系
    	authMapper.deleteUserRole(userRole);
        return authMapper.deleteUserById(userId) > 0;
    }

    @Override
    public boolean changePassword(Long userId, String password) {
        User user = getUerById(userId);
        user.setPassword(password);
        passwordHelper.encryptPassword(user);
        return updateUser(user);
    }

    @Override
    public User getUerById(Long id) {
        return authMapper.getUserById(id);
    }

    @Override
    public List<User> getUsers(User user) {
        return authMapper.getUsers(user);
    }

    @Override
    public List<User> getUsers(User user, PageInfo pageInfo) {
        ServerCM.startLocal("getUsers");
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<User> list = authMapper.getUsers(user);
        ServerCM.exit();
        return list;

    }

    @Override
    public User getUserByLoginName(String loginName) {
        ServerCM.startLocal("getUserByLoginName");
        try{
            String a = null;
            if(a.equals("aaa")){

            }
        }catch (Exception ex){
            ServerCM.exit(ex);

        }
        User user =  authMapper.getUserByLoginName(loginName);
        ServerCM.exit();
        return user;
    }


    @Override
    public Organization createOrganization(Organization organization) {
        authMapper.insertOrganization(organization);
        return organization;
    }

    @Override
    public boolean updateOrganization(Organization organization) {
        return authMapper.updateOrganizationById(organization) > 0;
    }

    @Override
    public boolean deleteOrganization(Long organizationId) {
        return authMapper.deleteOrganizationById(organizationId) > 0;
    }

    @Override
    public Organization getOrganizationById(Long organizationId) {
        return authMapper.getOrganizationById(organizationId);
    }

    @Override
    public List<Organization> getOrganizations(Organization organization) {

        return authMapper.getOrganizations(organization);
    }
    
    @Override
    @Transactional
    public boolean creteRoleResources(String roleId,String [] resources){
    	
    	//先删除本角色的权限
    	authMapper.deleteRoleResourceById(Long.valueOf(roleId));
    	int length = 0;
    	if(resources!=null){
    		length = resources.length;
    	}
    	//新增角色和菜单对照关系
    	if(length>0){
    		for(String resourceId:resources){
    			RoleResources roleRes = new RoleResources();
    			roleRes.setRoleId(Long.parseLong(roleId));
    			roleRes.setResourceId(Long.parseLong(resourceId));
    			authMapper.createRoleResource(roleRes);
    		}
    	}
    	
    	return true;
    }


}
