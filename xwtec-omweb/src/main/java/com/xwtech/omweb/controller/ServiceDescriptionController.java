package com.xwtech.omweb.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.ServiceDescription;
import com.xwtech.omweb.service.IServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by zhangq on 2017/3/28.
 */
@Controller
@RequestMapping("omweb/service")
public class ServiceDescriptionController {


    final static Logger logger = LoggerFactory.getLogger(ServiceDescriptionController.class);


    @Autowired
    IServiceDescription iServiceDescription;

    /**
     * @param ps
     * @param pn
     * @return
     */
    @RequestMapping(value = {"","/","index"})
    public ModelAndView index(@RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn,
                              String service) {
        PageInfo<ServiceDescription> pageInfo = new PageInfo<ServiceDescription>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setService(service);
        List<ServiceDescription> gridList = iServiceDescription.getAll(serviceDescription, pageInfo);

        return new ModelAndView("service/index").addObject("list", gridList)
                .addObject("pageInfo", ((Page<ServiceDescription>) gridList).toPageInfo());
    }


    @RequestMapping(value = "create", method = RequestMethod.GET)
    private ModelAndView create() {

        //ServiceDescription serviceDescription = new ServiceDescription();


        return new ModelAndView("service/create");
    }


    @RequestMapping(value = {"create"}, method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(
            String service,
            String description) {

        JSONResult jsonResult = new JSONResult();
        try {

            ServiceDescription serviceDescription = iServiceDescription.get(service);
            if (serviceDescription == null) {
                serviceDescription = new ServiceDescription();
                serviceDescription.setService(service);
                serviceDescription.setDescription(description);
                iServiceDescription.add(serviceDescription);
                jsonResult.setSuccessInfo("新增服务简介成功！");
            } else {
                jsonResult.setFailInfo("已存在的服务！");
            }

        } catch (Exception ex) {
            logger.error("新增服务简介", ex);
            jsonResult.setErrorInfo("新增服务简介异常");
        }

        return jsonResult;
    }


    @RequestMapping(value = {"delete"}, method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(
            String id) {

        JSONResult jsonResult = new JSONResult();
        try {

            int r = iServiceDescription.delete(id);
            if (r > 0) {
                jsonResult.setSuccessInfo("删除服务简介成功！");
            } else {
                jsonResult.setFailInfo("删除服务简介失败！");
            }


        } catch (Exception ex) {
            logger.error("删除服务简介", ex);
            jsonResult.setErrorInfo("删除服务简介异常");
        }

        return jsonResult;
    }


}
