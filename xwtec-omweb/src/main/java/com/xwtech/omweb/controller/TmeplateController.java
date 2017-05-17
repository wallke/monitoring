package com.xwtech.omweb.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.Room;
import com.xwtech.omweb.model.Server;
import com.xwtech.omweb.model.Template;
import com.xwtech.omweb.model.TemplateChannel;
import com.xwtech.omweb.service.TmeplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;


/**
 * Created by Administrator on 2017/3/7 0007.
 */
@Controller
@RequestMapping("omweb/template")
public class TmeplateController {

    @Autowired
    private TmeplateService templateService;

    @RequestMapping(value = "index",method ={ RequestMethod.GET,RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request,
                              String name, String loginName, @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn){
        PageInfo<Template> pageInfo = new PageInfo<Template>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<Template> templateList = templateService.queryTemplateList(pageInfo);
        return new ModelAndView("template/index").addObject("templateList", templateList)
                .addObject("pageInfo",((Page<Template>) templateList).toPageInfo());
    }


    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String template_id){
        String actionType ="";
        Template template = new Template();
        String emailMes = "";
        String smsMes = "";
        if (StringUtils.isNotEmpty(template_id))
        {
            template = templateService.queryTemplateDetail(template_id);
            List<TemplateChannel> templates = template.getTemplates();
            for (int i = 0; i < templates.size(); i++) {

                TemplateChannel templateChannel = templates.get(i);
                String channel = templateChannel.getChannel();
                if(channel.equals("sms"))
                {
                    smsMes = templateChannel.getMessage_template();
                }else
                {
                    emailMes= templateChannel.getMessage_template();
                }
            }
            actionType ="update";
        }else
        {
            actionType = "add";
        }
        
        return new ModelAndView("template/create").addObject("template",template).addObject("smsMes",smsMes).addObject("emailMes",emailMes)
                .addObject("actionType",actionType);
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public JSONResult insertTemplate(HttpServletRequest request,Template template) throws JsonProcessingException {
        JSONResult jsonResult  = new JSONResult();
        String actionType = request.getParameter("actionType");
        String[] channels = request.getParameterValues("channel");
        String[] message_templates = request.getParameterValues("message_template");
        try {

            if (actionType .equals("add"))
            {
                template.setTemplate_id(UUID.randomUUID().toString().replace("-",""));
                //保存mysql 和es
                int count = templateService.addTemplate(template, channels, message_templates);
                if (count >0)
                {
                    jsonResult.setSuccessInfo("新增模板成功");
                }else
                {
                    jsonResult.setFailInfo("新增模板失败");
                }
            }else
            {
                int count = templateService.updateTemplate(template, channels, message_templates);
                if (count >0)
                {
                    jsonResult.setSuccessInfo("修改模板成功");
                }else
                {
                    jsonResult.setFailInfo("修改模板失败");
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("操作模板异常");
        }
        return jsonResult;
    }

    @RequestMapping(value = "delete",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteTemplate(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        try{
            String template_id = request.getParameter("template_id");
            if (StringUtil.isNotEmpty(template_id)){
                int count = templateService.deleteTemplate(template_id);
                if (count >0){
                    jsonResult.setSuccessInfo("删除模板成功");
                }else{
                    jsonResult.setSuccessInfo("删除模板失败");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            jsonResult.setFailInfo("删除模板异常");
        }

        return  jsonResult;
    }

}
