package com.xwtech.omweb.controller;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.Crop;
import com.xwtech.omweb.model.CropContacts;
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
import java.util.List;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
@Controller
@RequestMapping("omweb/crop")
public class CropController {
    private final static Logger logger = LoggerFactory.getLogger(CropController.class);

    @Resource(name = "cropService")
    private ICropService cropService;

    //公司联系人
    @Resource(name = "cropContactsService")
    private ICropContactsService cropContactsService;
    /**
     * 查询公司信息列表
     * @param request
     * @param cropName
     * @param ps
     * @param pn
     * @return
     */
    @RequestMapping(value = "index",method ={ RequestMethod.GET,RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request,
                              String cropName, @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn){
        PageInfo<Crop> pageInfo = new PageInfo<Crop>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);

        List<Crop> cropsList = cropService.queryCorpList(cropName, pageInfo);
        return new ModelAndView("crop/index").addObject("cropsList", cropsList).addObject("cropName", cropName)
                .addObject("pageInfo",((Page<Crop>) cropsList).toPageInfo());
    }

    /**
     * 跳转公司新增修改页面
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String cropId){
        String actionType = "create";

        Crop crop = new Crop();
        List<CropContacts> cropContactsesList = null;
        if (StringUtils.isNotEmpty(cropId)) {
            actionType= "modfiy";
            //根据id查看公司详情
            crop = cropService.queryCropById(cropId);
            //根据公司ID查询所有联系人信息
            cropContactsesList = cropContactsService.queryCropContactsList(cropId);
        }
        return new ModelAndView("crop/create").addObject("actionType",actionType).addObject("crop",crop).addObject("cropContactsesList",cropContactsesList);

    }

    @RequestMapping(value = "cropDetail",method = RequestMethod.GET)
    public ModelAndView cropDetail(String cropId){
        Crop  crop = cropService.queryCropById(cropId);
        //根据公司ID查询所有联系人信息
        List<CropContacts> cropContactsesList = cropContactsService.queryCropContactsList(cropId);
        return new ModelAndView("crop/detail").addObject("crop",crop).addObject("cropContactsesList",cropContactsesList);

    }

    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute Crop crop){
        String actionType = request.getParameter("actionType");
        JSONResult jsonResult = new JSONResult();
        User user = (User)request.getAttribute(Constants.CURRENT_USER);
        if(user != null )
        {
            crop.setCreateManCode(String.valueOf(user.getId()));
            crop.setCreateManName(user.getLoginName());
            crop.setLastManCode(String.valueOf(user.getId()));
            crop.setLastManName(user.getLoginName());
            crop.setCreateMemo("");
            crop.setLastMemo("");
        }
        try
        {
            int cou = cropService.queryCropNameIsExist(StringUtils.isNoneEmpty(crop.getCropId()) ? crop.getCropId() : null, crop.getCropName());
            if (cou > 0)
            {
                jsonResult.setFailInfo("公司名称已经存在,请修正");
                return jsonResult;
            }

            if ("create".equals(actionType))
            {
                //新增
                int count = cropService.addCorp(crop);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("新增公司成功！");
                }else
                {
                    jsonResult.setFailInfo("新增公司失败，请稍后再试！");
                }
            }else if ("modfiy".equals(actionType))
            {
                //修改
                int count = cropService.updateCrop(crop);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改公司成功！");
                }else
                {
                    jsonResult.setFailInfo("修改公司失败，请稍后再试！");
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
     * 根据公司ID删除公司信息
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteCrop",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteCrop(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String cropId = request.getParameter("cropId");
        if (cropId !=null && !cropId.equals(""))
        {
            List<CropContacts> cropContactses = cropContactsService.queryCropContactsList(cropId);
            if (cropContactses !=null && cropContactses.size()>0)
            {
                jsonResult.setFailInfo("该公司下面存在联系人信息，删除公司失败");
                return  jsonResult;
            }
            try {
                int count = cropService.deleteCrop(cropId);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除公司成功！");
                }else
                {
                    jsonResult.setFailInfo("删除公司失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除公司异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除公司参数异常，请稍后再试！");
        }

        return jsonResult;
    }

}
