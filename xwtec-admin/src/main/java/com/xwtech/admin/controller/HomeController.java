package com.xwtech.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by zq on 16/9/9.
 */
@RestController
public class HomeController {



    /**
     *
     *
     * @param
     * @return
     */
    @RequestMapping("/")
    public ModelAndView index() {

        return  new ModelAndView("index");
    }


    @RequestMapping("hasRoles")
    //@RequiresRoles("superamdin1")
    public ModelAndView hasRole() {
        return  new ModelAndView("hasRoles");
    }


    @RequestMapping("/aa")
    public ModelAndView aa(){




        return  new ModelAndView("index");
    }


}
