package com.xwtech.omweb.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.*;
import com.xwtech.omweb.service.IAppService;
import com.xwtech.omweb.service.ICropContactsService;
import com.xwtech.omweb.service.ICropService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
@Controller
@RequestMapping("omweb/cropContacts")
public class CropContactsController {

    private final static Logger logger = LoggerFactory.getLogger(CropContactsController.class);

    //公司联系人
    @Resource(name = "cropContactsService")
    private ICropContactsService cropContactsService;

    //公司
    @Resource(name = "cropService")
    private ICropService cropService;

    //应用
    @Resource(name = "appService")
    private IAppService appService;

    @RequestMapping(value = "index",method ={ RequestMethod.GET,RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request, String name, String email ,
                              String mobile, String cropName,
                              @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn){

        PageInfo<CropContacts> pageInfo = new PageInfo<CropContacts>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);

        //人员信息列表
        List<CropContacts> cropContactsesList = cropContactsService.queryCropContacts(name, email, mobile, cropName,pageInfo);
        //所有公司
        List<Crop> cropList = cropService.getCorpList();
        return new ModelAndView("contactses/index").addObject("cropContactsesList", cropContactsesList).addObject("name", name)
                .addObject("email",email).addObject("mobile",mobile)
                .addObject("cropName",cropName).addObject("cropList",cropList)
                .addObject("pageInfo",((Page<CropContacts>) cropContactsesList).toPageInfo());

    }

    /**
     * 跳转人员信息新增修改页面
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String contactId){
        String actionType = "create";
        CropContacts cropContacts = new CropContacts();
        //所有公司
        List<Crop> cropList = cropService.getCorpList();
        List<App>  appList = null;
        if (StringUtils.isNotEmpty(contactId)) {
            actionType= "modfiy";
            cropContacts = cropContactsService.queryCropContactsDetail(contactId);
        }
        return new ModelAndView("contactses/create").addObject("actionType",actionType)
                  .addObject("cropContacts",cropContacts).addObject("cropList",cropList).addObject("appList",appList);
    }

    /**
     * 跳转人员信息应用维护新增修改页面
     * @return
     */
    @RequestMapping(value = "createAppContact", method = RequestMethod.GET)
    public ModelAndView createAppContact(String contactId ,@RequestParam(name = "ps", defaultValue = "10") int ps,
    @RequestParam(name = "pn", defaultValue = "1") int pn){
        //根据联系人ID查询维护应用信息
        PageInfo<App> pageInfo = new PageInfo<App>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<App> appList = new ArrayList<App>();
        //所有应用,用于模态框选择
        List<App> appAllList= appService.queryAppListNoBindContacts(contactId);
        if (StringUtils.isNotEmpty(contactId) )
        {
            appList =  appService.queryAllContactList(contactId,pageInfo);
        }
        return new ModelAndView("contactses/createApp").addObject("appList",appList).addObject("appAllList",appAllList)
                    .addObject("contactId",contactId)
                    .addObject("pageInfo",((Page<App>) appList).toPageInfo());
    }

    /**
     * 新增人员信息或修改主机人员信息
     * @param request
     * @param cropContacts
     * @return
     */
    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute CropContacts cropContacts){
        String actionType = request.getParameter("actionType");
        JSONResult jsonResult = new JSONResult();
        User user = (User)request.getAttribute(Constants.CURRENT_USER);
        if (user != null )
        {
            cropContacts.setCreateManCode(String.valueOf(user.getId()));
            cropContacts.setCreateManName(user.getLoginName());
            cropContacts.setLastManCode(String.valueOf(user.getId()));
            cropContacts.setLastManName(user.getLoginName());
            cropContacts.setCreateMemo("");
            cropContacts.setLastMemo("");
        }else
        {
            jsonResult.setFailInfo("用户信息未获取到,请稍后再试");
            return jsonResult;
        }
        int c = cropContactsService.validateUserName(StringUtils.isNoneEmpty(cropContacts.getContactId()) ? cropContacts.getContactId() : null, cropContacts.getUserName());
        if (c > 0)
        {
            jsonResult.setFailInfo("用户账户已经存在,请修正");
            return jsonResult;
        }
        String type = cropContacts.getType();
        String[] split = type.split(",");
        for (String channel :split) {
            if (channel.equals("sms"))
            {
                 int i = cropContactsService.validateParam(StringUtils.isNoneEmpty(cropContacts.getContactId()) ? cropContacts.getContactId() : null, channel, cropContacts.getMobile());
                if (i > 0)
                {
                    jsonResult.setFailInfo("手机号码已经存在,请修正");
                    return jsonResult;
                }
            }
            if (channel.equals("email"))
            {
                int i = cropContactsService.validateParam(StringUtils.isNoneEmpty(cropContacts.getContactId()) ? cropContacts.getContactId() : null, channel, cropContacts.getEmail());
                if (i > 0)
                {
                    jsonResult.setFailInfo("邮箱已经存在,请修正");
                    return jsonResult;
                }
            }
        }
        try
        {
            if ("create".equals(actionType))
            {
                //新增
                String contactId = UUID.randomUUID().toString().replace("-", "");
                cropContacts.setContactId(contactId);
                int count = cropContactsService.addCropContacts(cropContacts);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("新增人员信息成功！");
                    jsonResult.setData(contactId);
                }else
                {
                    jsonResult.setFailInfo("新增人员信息失败，请稍后再试！");
                }
            }else if ("modfiy".equals(actionType))
            {
                //修改
                int count = cropContactsService.updateCropContacts(cropContacts);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("修改人员信息成功！");
                }else
                {
                    jsonResult.setFailInfo("修改人员信息失败，请稍后再试！");
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
        }

        return jsonResult;
    }

    /**
     * 根据应用ID删除应用
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteCropContacts",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteCropContacts(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String contactId = request.getParameter("contactId");
        String name = request.getParameter("name");
        if (contactId !=null && !contactId.equals(""))
        {
            try {
                int count = cropContactsService.deleteCropContacts(contactId,name);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除人员信息成功！");
                }else
                {
                    jsonResult.setFailInfo("删除人员信息失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除人员信息异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除人员信息参数异常，请稍后再试！");
        }

        return jsonResult;
    }

    /**
     * 新增联系人维护应用
     * @param request
     * @return
     */
    @RequestMapping(value = "addContactApp")
    @ResponseBody
    public JSONResult addContactApp(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String contactId = request.getParameter("contactId");
        String[] appIdses = request.getParameterValues("appIds");
        if (contactId != null && !contactId.equals("") && appIdses.length > 0)
        {
            try{
                int count = cropContactsService.addAppContact(contactId, appIdses);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("新增维护应用成功！");
                }else
                {
                    jsonResult.setFailInfo("新增维护应用失败！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("新增维护应用异常,请稍后再试");
            }
        }else
        {
            jsonResult.setFailInfo("新增维护应用参数异常,请稍后再试");
        }

       return  jsonResult;
    }

    /**
     * 删除联系人维护应用
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteAppContact")
    @ResponseBody
    public JSONResult deleteAppContact(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String contactId = request.getParameter("contactId");
        String appId = request.getParameter("appId");
        if (contactId != null && !contactId.equals("") && appId !=null && !appId.equals(""))
        {
            try{
                int count = cropContactsService.deleteAppContact(contactId, appId);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("删除维护应用成功！");
                }else
                {
                    jsonResult.setFailInfo("删除维护应用失败！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除维护应用异常,请稍后再试");
            }
        }else
        {
            jsonResult.setFailInfo("删除维护应用参数异常,请稍后再试");
        }

        return  jsonResult;
    }

}
