package com.xwtech.admin.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.framework.shiro.model.auth.*;

import java.util.List;
import java.util.Set;

/**
 * Created by zq on 16/8/25.
 */
public interface IAuthService {

    List getPlatforms(int available);

    Platform getPlatform(String code);

    App createApp(App app);

    boolean updateApp(App app);

    boolean deleteApp(Long appId);

    App getApp(Long appId);

    List<App> getApps();

    List getApps(App app, PageInfo pageInfo);

    /**
     * 根据appKey查找AppId
     * @param appKey
     * @return
     */
    Long getAppIdByCode(String appKey);

    App getAppByCode(String appKey);




    Menu createResource(Menu resource);

    boolean updateResource(Menu resource);

    boolean deleteResource(Long resourceId);

    Menu getResourceById(Long resourceId);

    List<Menu> getResources(Menu resource);
    
    List<Menu> getResourcesByRoleRes(RoleResources roleRes);
    
    List<RoleResources> getRoleResource(RoleResources roleRes);
    
    
    List<Menu> getResourcesByUserId(long userId);


    /**
     * 根据用户权限得到菜单
     * @param permissions
     * @return
     */
    List<Menu> getMenus(List<String> permissions);







    Role createRole(Role role);

    boolean updateRole(Role role);

    boolean deleteRole(Long roleId);
    
    boolean deleteUserRole(UserRole userRole);

    Role getRoleById(Long roleId);
    
    Role getRoleByName(String name);
    
    Role getRoleByCode(String code);

    List<Role> getRoles();
    
    List<UserRole> getUserRoleByUserId(Long userId);

    List<Role> getRoles(Role role, PageInfo pageInfo);

    /**
     * 根据角色编号得到角色标识符列
     * @param roleIds
     * @return
     */
    List<String> getRoles(Long... roleIds);


    /**
     *
     * @param loginName
     * @return
     */
    Set<String> getPermissions(String loginName);



    Set<String> getRoles(String loginName);







    /**
     * 创建用户
     * @param user
     */
    User createUser(User user);
    
    User createUser(User user,String roles);

    boolean updateUser(User user);
    
    boolean updateUser(User user,String rolse);

    boolean deleteUser(Long userId);

    /**
     * 修改密码
     * @param userId
     * @param password
     */
    boolean changePassword(Long userId, String password);


    User getUerById(Long id);
    
    List<User> getUsers(User user);


    List<User> getUsers(User user, PageInfo pageInfo);

    /**
     * 根据用户名查找用
     * @param loginName
     * @return
     */
    User getUserByLoginName(String loginName);

    Organization createOrganization(Organization organization);

    boolean updateOrganization(Organization organization);

    boolean deleteOrganization(Long organizationId);

    Organization getOrganizationById(Long organizationId);

    List<Organization> getOrganizations(Organization organization);

    boolean creteRoleResources(String roleId,String[] resources);

}
