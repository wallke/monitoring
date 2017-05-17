package com.xwtech.admin.dao;

import com.xwtech.framework.shiro.model.auth.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by zq on 16/8/25.
 */
@Mapper
public interface AuthMapper {


    List<Platform> getPlatforms(Platform platform);

    void insertApp(App app);

    int updateAppById(App app);

    App getAppById(Long id);

    List getApps(App app);


    Long getAppIdByCode(String code);

    App getAppByCode(String code);

    int deleteAppById(Long id);


    int deleteResourceById(Long id);

    void insertResource(Menu record);

    Menu getResourceById(Long id);

    int updateResourceById(Menu record);

    List<Menu> getResources(Menu record);
    
    List<Menu> getResourcesByRoleRes(RoleResources roleRes);
    
    List<RoleResources> getRoleResource(RoleResources roleRes);

    List<Menu> getResourcesByUserId(Long userId);


    int deleteRoleById(Long id);
    
    int deleteUserRole(UserRole userRole);

    void insertRole(Role record);

    Role getRoleById(Long id);
    
    Role getRoleByName(String name);
    
    Role getRoleByCode(String code);

    int updateRoleById(Role record);
    
    int insertUserRole(UserRole userRole);

    List<Role> getRoles(Role role);

    List<Role> getRolesByUserId(Long userId);

    List<UserRole> getUserRoleByUserId(Long userId);


    int deleteUserById(Long id);

    int insertUser(User record);

    User getUserById(Long id);

    int updateUserById(User record);

    List<User> getUsers(User record);

    User getUserByLoginName(String loginName);


    int deleteOrganizationById(Long id);

    int insertOrganization(Organization record);

    Organization getOrganizationById(Long id);

    int updateOrganizationById(Organization record);

    List<Organization> getOrganizations(Organization organization);
    
    int deleteRoleResourceById(Long roleId);
    
    int createRoleResource(RoleResources resource);

}
