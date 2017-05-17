package com.xwtech.omweb.controller;

import com.github.pagehelper.PageInfo;
import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.AppCategory;
import com.xwtech.omweb.service.IAppCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
@Controller
@RequestMapping("omweb/appCategoryry")
public class AppCategoryryController {
    private final static Logger logger = LoggerFactory.getLogger(AppController.class);

    //应用类别
    @Resource(name = "appCategoryService")
    private IAppCategoryService appCategoryService;


    @RequestMapping(value = "index",method ={ RequestMethod.GET,RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request, String JBNum,
                              @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn){

        PageInfo<AppCategory> pageInfo = new PageInfo<AppCategory>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);

        //获取最高级别的应用类别
        List<AppCategory> appCategoriesList1 = appCategoryService.queryCategoryList(null,null);

        List<AppCategory> arrayList = new ArrayList<AppCategory>();
        List<AppCategory> subs1 = new  ArrayList<AppCategory>();
        List<AppCategory> subs2 = new  ArrayList<AppCategory>();
        List<AppCategory> subs = new  ArrayList<AppCategory>();
        if (appCategoriesList1 != null) {
            for (AppCategory appCategory : appCategoriesList1) {
                if(Integer.valueOf(appCategory.getJBNum()) == 0){
                     subs =   appCategoriesList1.stream().filter(x -> Integer.valueOf(x.getJBNum()) == Integer.valueOf(appCategory.getCategoryId())).collect(Collectors.toList());
                    if(subs != null){
                        for (AppCategory appCategory1:subs){
                             subs1 = appCategoriesList1.stream().filter(x -> Integer.valueOf(x.getJBNum())== Integer.valueOf(appCategory1.getCategoryId())).collect(Collectors.toList());
                            if(subs1 != null){
                                for(AppCategory appCategory2 :subs1){
                                    subs2 = appCategoriesList1.stream().filter(x -> Integer.valueOf(x.getJBNum()) == Integer.valueOf(appCategory2.getCategoryId())).collect(Collectors.toList());
                                    if(subs2 != null){
                                        appCategory2.setSubAppCategory(subs2);//4级按钮
                                    }
                                }
                                appCategory1.setSubAppCategory(subs1);//3级按钮
                            }
                        }
                        appCategory.setSubAppCategory(subs);//2级按钮
                    }
                    arrayList.add(appCategory);//一级菜单
                }
            }
        }
        return new ModelAndView("appCategoryry/index")
                .addObject("arrayList",arrayList)
                .addObject("JBNum", JBNum);

    }

   @RequestMapping(value = "addAppCategoryry",method = RequestMethod.POST)
   @ResponseBody
    public JSONResult addAppCategoryry(HttpServletRequest request,@ModelAttribute AppCategory appCategory){
       JSONResult jsonResult = new JSONResult();
       User user = (User)request.getAttribute(Constants.CURRENT_USER);
       if (user != null )
       {
           appCategory.setCreateManCode(String.valueOf(user.getId()));
           appCategory.setCreateManName(user.getLoginName());
           appCategory.setLastManCode(String.valueOf(user.getId()));
           appCategory.setLastManName(user.getLoginName());
           appCategory.setCreateMemo("");
           appCategory.setLastMemo("");
       }else
       {
           jsonResult.setFailInfo("用户信息未获取到,请稍后再试");
           return jsonResult;
       }
       String accountType = request.getParameter("accountType");
       if(accountType.equals("add"))
       {
           appCategory.setJB(appCategory.getJB()+1);
           int count = appCategoryService.addAppCategory(appCategory);
           if(count > 0 )
           {
               jsonResult.setSuccessInfo("新增应用类别成功！");
           }else
           {
               jsonResult.setFailInfo("新增类别失败，请稍后再试！");
           }
       }else
       {
           int count = appCategoryService.updateAppCategory(appCategory);
           if(count > 0 )
           {
               jsonResult.setSuccessInfo("修改应用类别成功！");
           }else
           {
               jsonResult.setFailInfo("修改用类别失败，请稍后再试！");
           }
       }

       return  jsonResult;

   }


   @RequestMapping(value = "deleteCategoryry",method = RequestMethod.POST)
   @ResponseBody
   public JSONResult deleteCategoryry(HttpServletRequest request){
       JSONResult jsonResult = new JSONResult();
       String categoryId = request.getParameter("categoryId");
       try
       {
           //根据应用节点查询该节点下面是否有节点
           int count = appCategoryService.queryAppCategoryCount(categoryId);
           if(count >0 )
           {
               jsonResult.setFailInfo("删除节点失败,清先删除其挂载节点");
           }else
           {
               count = appCategoryService.deleteAppCategory(categoryId);
               if (count >0 )
               {
                   jsonResult.setSuccessInfo("删除节点成功");
               }else
               {
                   jsonResult.setFailInfo("删除节点失败");
               }
           }
       }catch (Exception e)
       {
           e.printStackTrace();
           jsonResult.setFailInfo("删除节点异常，请稍后再试");
       }

      return jsonResult;
   }



}
