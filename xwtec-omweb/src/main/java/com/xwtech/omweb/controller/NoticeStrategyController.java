package com.xwtech.omweb.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.*;
import com.xwtech.omweb.service.ICropContactsService;
import com.xwtech.omweb.service.IFrequencyModeService;
import com.xwtech.omweb.service.INoticeStrategyService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/3/15 0015.
 * 通知策略控制层
 */
@Controller
@RequestMapping("omweb/noticeStrategy")
public class NoticeStrategyController {
    @Autowired
    private INoticeStrategyService noticeStrategyService;
    //公司联系人
    @Resource(name = "cropContactsService")
    private ICropContactsService cropContactsService;
    //变频规则
    @Autowired
    private IFrequencyModeService frequencyModeService;
    /**
     * 通知策略列表
     * @param request
     * @param cropName
     * @param ps
     * @param pn
     * @return
     */
    @RequestMapping(value = "index",method ={ RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request,
                              String cropName, @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn){
        PageInfo<NoticeStrategy> pageInfo = new PageInfo<NoticeStrategy>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<NoticeStrategy> noticeStrategieList = noticeStrategyService.queryNoticeStrategyList(pageInfo);
        return new ModelAndView("noticeStrategy/index").addObject("noticeStrategieList", noticeStrategieList)
                .addObject("pageInfo",((Page<NoticeStrategy>) noticeStrategieList).toPageInfo());
    }

    /**
     * 跳转修改或新增通知策略
     * @param notice_strategy_code
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String notice_strategy_code){
        String actionType = "create";
        List<ContactsChannel> cropContactList = cropContactsService.queryContactsChannelList();
        List<FrequencyMode> frequencyModeList = frequencyModeService.queryFrequencyMode(null);
        NoticeStrategy noticeStrategy = new NoticeStrategy();
        ContactsChannel channelFrequency = new ContactsChannel();
        if (StringUtils.isNotEmpty(notice_strategy_code)) {
            actionType= "modfiy";
            noticeStrategy = noticeStrategyService.queryNoticeStrategyDetail(notice_strategy_code);
            channelFrequency = noticeStrategyService.getChannelFrequency(notice_strategy_code);
        }
        return new ModelAndView("noticeStrategy/create").addObject("actionType",actionType)
                .addObject("cropContactList",cropContactList).addObject("frequencyModeList",frequencyModeList)
                .addObject("channelFrequency",channelFrequency)
                .addObject("noticeStrategy",noticeStrategy);
    }


    /**
     * 新增修改通知策略
     * @param request
     * @return
     */
    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute NoticeStrategy noticeStrategy){
        String actionType = request.getParameter("actionType");

        String[] frequencyLists = request.getParameter("frequencyList").split(",");
        String[] channelIdLists = request.getParameter("channelIdList").split(",");
        JSONResult jsonResult = new JSONResult();
        try
        {
            if ("create".equals(actionType))
            {
                //新增
                noticeStrategy.setNotice_strategy_code(String.valueOf(System.currentTimeMillis()));
                int count = noticeStrategyService.addNoticeStrategy(noticeStrategy,frequencyLists,channelIdLists);

                if(count > 0)
                {
                    jsonResult.setSuccessInfo("新增通知策略成功！");
                }else
                {
                    jsonResult.setFailInfo("新增通知策略失败，请稍后再试！");
                }
            }else if ("modfiy".equals(actionType))
            {
                //修改
                int count = noticeStrategyService.updateNoticeStrategy(noticeStrategy,frequencyLists,channelIdLists);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改通知策略成功！");
                }else
                {
                    jsonResult.setFailInfo("修改通知策略失败，请稍后再试！");
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
     * 根据ID删除通知策略
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteNoticeStrategy",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteNoticeStrategy(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String notice_strategy_code = request.getParameter("notice_strategy_code");
        if (notice_strategy_code !=null && !notice_strategy_code.equals(""))
        {
            try {
                int count = noticeStrategyService.deleteNoticeStrategy(notice_strategy_code);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除通知策略成功！");
                }else
                {
                    jsonResult.setFailInfo("删除通知策略失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除通知策略异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除通知策略参数异常，请稍后再试！");
        }

        return jsonResult;
    }


    @RequestMapping("getContactFrequency")
    @ResponseBody
    public JSONResult getContactFrequency(HttpServletRequest request,String contact_id,String frequency_code){
        JSONResult jsonResult = new JSONResult();
        //联系人信息
        CropContacts cropContacts = cropContactsService.queryCropContactsDetail(contact_id);
        FrequencyMode frequencyMode = frequencyModeService.queryFrequencyModeDetail(frequency_code);
        cropContacts.setFrequency_name(frequencyMode.getFrequency_name());
        jsonResult.setData(cropContacts);
        return jsonResult;
    }

//    @RequestMapping(value = "appNoticeStrategyModeEs")
//    @ResponseBody
//    public JSONResult appNoticeStrategyModeEs(String id) throws JsonProcessingException {
//        JSONResult jsonResult = new JSONResult();
//        NoticeStrategy noticeStrategy = noticeStrategyService.queryNoticeStrategyDetail(id);
//        List<CropContacts> cropContactses = cropContactsService.queryCropContactsByNoticeId(id);
//        for (int i = 0; i <cropContactses.size() ; i++) {
//            CropContacts cropContacts = cropContactses.get(i);
//            List<CropContacts.Channels> channelsList = cropContacts.getChannels();
//            String type = cropContacts.getType();
//            if (StringUtils.isNotEmpty(type)){
//                String[] split = type.split(",");
//                for (int i1 = 0;  i1<split.length ; i1++) {
//                    CropContacts.Channels channels = new CropContacts.Channels();
//                    type = split[i1];
//                    if (type.equals("sms"))
//                    {
//                        channels.setAddress(cropContacts.getMobile());
//                    }else if (type.equals("email"))
//                    {
//                        channels.setAddress(cropContacts.getEmail());
//                    }
//                    channels.setType(type);
//                    channelsList.add(channels);
//                }
//            }
//        }
//        noticeStrategy.setStakeholders(cropContactses);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        String json = objectMapper.writeValueAsString(noticeStrategy);
//        System.out.print(json);
//
//        return jsonResult;
//    }



}
