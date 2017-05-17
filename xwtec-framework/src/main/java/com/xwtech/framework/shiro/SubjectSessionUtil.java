package com.xwtech.framework.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Created by zhangq on 2017/1/11.
 */
public class SubjectSessionUtil {


    public static final String USER_SESSION_ATTRIBUTE_KEY = "center::auth::obj_user";


    public static final String MENU_SESSION_ATTRIBUTE_KEY = "center::auth::obj_menu";


    public static final String APP_SESSION_ATTRIBUTE_KEY = "center::auth::obj_app";

    public static final String PLATFORM_SESSION_ATTRIBUTE_KEY = "center::auth::obj_platform";

    public static final String CURRENT_PLATFORM_ID_SESSION_ATTRIBUTE_KEY = "center::auth::obj_platform_id";



}
