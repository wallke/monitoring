package com.xwtech.omweb.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.service.AlarmService;
import com.xwtech.es.service.ApplicationService;
import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.enums.SdkFlag;
import com.xwtech.omweb.model.*;
import com.xwtech.omweb.service.IAppService;
import com.xwtech.omweb.service.SDKService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/2/20 0020.
 */
@Controller
@RequestMapping("omweb/sdk")
public class SdkController {

    @Resource(name = "sdkService")
    private SDKService sdkService;
    //应用
    @Resource(name = "appService")
    private IAppService appService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private AlarmService alarmService;


    @RequestMapping(value = "querySdkJson",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult  querySdkJson(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String confId = request.getParameter("confId");
        SDK sdk = sdkService.querySdkDetail(confId);
        if (sdk != null)
        {
            String flag = sdk.getFlag();
            if (flag.equals(SdkFlag.ONE.getFlag()))
            {
                try
                {
                    SdkJson sdkJson = sdkService.querySdkJson(confId);
                    List<SDKConfEnv> envirList = sdkJson.getEnvirs();
                    if (envirList !=null && envirList.size()>0)
                    {
                        for (int i = 0; i <envirList.size() ; i++) {
                            SDKConfEnv sdkConfEnv = envirList.get(i);
                            String nodeCodes = sdkConfEnv.getNodeCodes();
                            if (org.apache.commons.lang3.StringUtils.isNotEmpty(nodeCodes))
                            {
                                String[] split = nodeCodes.split(",");
                                sdkConfEnv.setNode_codes(split);
                            }
                        }
                    }
                    String id = "servercm_" + sdkJson.getLog_ver()+"_"+sdkJson.getAppServer_name()+"_" +sdkJson.getAppServer_ver();
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    String json = objectMapper.writeValueAsString(sdkJson);
                    String updateTime = getUpdateTime(sdkJson.getLog_ver());
                    String s = StringEscapeUtils.unescapeJava(json);
                    int statusCode = alarmService.postSdk(id, s);
                    if (statusCode >= 200 && statusCode <300)
                    {
                        int status = alarmService.postDate(updateTime);
                        if (status >= 200 && status <300)
                        {
                            jsonResult.setSuccessInfo("应用成功");
                        }else
                        {
                            jsonResult.setFailInfo("应用失败");
                        }
                    }else
                    {
                        jsonResult.setFailInfo("应用失败");
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                    jsonResult.setFailInfo("应用失败");
                }
            }else
            {
                jsonResult.setFailInfo("此应用已经废弃，无法推送");
            }
        }

        return jsonResult;
    }

    /**
     * 封装JSON串
     * @param log_ver
     * @return
     */
    public String getUpdateTime(String log_ver){
//        String json ="{\"updates\":[{\"log_ver\": \"1.1\",\"update_time\": \"20160115203559\"}]}";
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONArray objects = new JSONArray();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String format = df.format(new Date());
        jsonObject.put("log_ver",log_ver);
        jsonObject.put("update_time",format);
        objects.add(jsonObject);
        jsonObject1.put("updates",objects);
        String s = jsonObject1.toString();
        return s ;
    }

    /**
     * sdk列表
     * @param request
     * @param ps
     * @param pn
     * @return
     */
    @RequestMapping(value = "index",method ={ RequestMethod.GET,RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request,
                              @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn){
        PageInfo<SDK> pageInfo = new PageInfo<SDK>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<SDK> sdkList = sdkService.querySDKList(pageInfo);
        return new ModelAndView("sdk/index").addObject("sdkList", sdkList)
                .addObject("pageInfo",((Page<SDK>) sdkList).toPageInfo());
    }

    @RequestMapping("create")
    public  ModelAndView create(HttpServletRequest request){
        List<App> appList = appService.getAppList();
        return new ModelAndView("sdk/create").addObject("appList", appList);
    }

    @RequestMapping("update")
    public  ModelAndView update(HttpServletRequest request){
        String confId = request.getParameter("confId");
        //根据ID查询SDK详细信息
        SDK sdk = sdkService.querySdkDetail(confId);
        //性能配置列表
        List<SDKConfHeart> sdkConfHeartList = sdkService.querySDKConfHeartByConfId(confId);
        //主机配置列表
        List<SDKConfHost> sdkConfHostList = sdkService.querySDKConfHostListByConfId(confId);
        //微环境配置列表
        List<SDKConfEnv> sdkConfEnvList = sdkService.querySDKConfEnvList(confId);
        //应用列表
        List<App> appList = appService.getAppList();
        return new ModelAndView("sdk/update").addObject("sdk",sdk).addObject("sdkConfHeartList", sdkConfHeartList)
                   .addObject("sdkConfHostList",sdkConfHostList)
                   .addObject("sdkConfEnvList",sdkConfEnvList)
                   .addObject("appList", appList);
    }

    @RequestMapping(value = "updateSdk",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult updateSdk(HttpServletRequest request,SDK sdk){
        JSONResult jsonResult = new JSONResult();
        User user = (User)request.getAttribute(Constants.CURRENT_USER);
        if (user != null )
        {
            sdk.setUpdateId(String.valueOf(user.getId()));
            sdk.setUpdateName(user.getName());
            try {
                if(StringUtils.isEmpty(sdk.getCollectAll()))
                {
                    sdk.setCollectAll("0");
                }else
                {
                    sdk.setCollectAll("1");
                }
                int count = sdkService.updateSDK(sdk);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改SDK配置成功！");
                }else
                {
                    jsonResult.setFailInfo("修改SDK配置失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("修改SDK配置异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("获取当前用户异常，请稍后再试！");
        }
        return jsonResult;
    }
    
    @RequestMapping(value = "addSDK",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult addSDK(HttpServletRequest request,SDK sdk){
        JSONResult jsonResult = new JSONResult();
        User user = (User)request.getAttribute(Constants.CURRENT_USER);
        if (user != null )
        {
            sdk.setCreateId(String.valueOf(user.getId()));
            sdk.setCreateName(user.getName());
            sdk.setUpdateId(String.valueOf(user.getId()));
            sdk.setUpdateName(user.getName());
            try {
                sdk.setConfId(UUID.randomUUID().toString().replace("-",""));

                String levelTwo = sdk.getLevelTwo();


                if(StringUtils.isEmpty(sdk.getCollectAll()))
                {
                    sdk.setCollectAll("0");
                }else
                {
                    sdk.setCollectAll("1");
                }
                int count = sdkService.addSDK(sdk);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("新增SDK配置成功！");
                }else
                {
                    jsonResult.setFailInfo("新增SDK配置失败，请稍后再试！");
                    System.out.print(111);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("新增SDK配置异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("获取当前用户异常，请稍后再试！");
        }
       return jsonResult;
    }

    @RequestMapping(value = "updateSdkFlag",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult  updateSdkFlag(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String confId = request.getParameter("confId");
        String appServerName = request.getParameter("appServerName");
        String flag = request.getParameter("flag");
        try
        {
            int count = sdkService.updateSdkFlag(confId, appServerName, flag);
            if (count >0)
            {
                jsonResult.setSuccessInfo(flag.equals(SdkFlag.ONE.getFlag())?"废弃成功":"启动成功");
            }else
            {
                jsonResult.setFailInfo("操作失败，请稍后再试！");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("操作异常，请稍后再试！");
        }
        return jsonResult;
    }


    /**
     * 新增SDK性能配置
     * @param request
     * @param sdkConfHeart
     * @return
     */
    @RequestMapping(value = "saveSdkHeart",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult  saveSdkHeart(HttpServletRequest request,SDKConfHeart sdkConfHeart){
        JSONResult jsonResult = new JSONResult();
        try {
            String heartId = sdkConfHeart.getHeartId();
            if (StringUtils.isEmpty(heartId))
            {
                sdkConfHeart.setHeartId(UUID.randomUUID().toString().replace("-",""));
                int count = sdkService.addSdkHeart(sdkConfHeart);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("新增性能配置成功！");
                }else
                {
                    jsonResult.setFailInfo("新增性能配置失败，请稍后再试！");
                }
            }else
            {
                int count = sdkService.updateSdkHrart(sdkConfHeart);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("修改性能配置成功！");
                }else
                {
                    jsonResult.setFailInfo("修改性能配置失败，请稍后再试！");
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("操作性能配置异常！");
        }
        return jsonResult;
    }


    /**
     * 查询性能配置信息
     * @param request
     * @return
     */
    @RequestMapping(value = "sdkConfHeartDetail",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult sdkConfHeartDetail(HttpServletRequest request){
        String heartId = request.getParameter("heartId");
        JSONResult jsonResult = new JSONResult();
        try {
            SDKConfHeart sdkConfHeart = sdkService.querySDKConfHeartDetail(heartId);
            jsonResult.setSuccessInfo("查询性能配置信息成功！");
            jsonResult.setData(sdkConfHeart);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return jsonResult;
    }

    /**
     * 根据ID删除性能配置
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteSdkConfHeart",method = RequestMethod.POST)
    @ResponseBody
    public  JSONResult deleteSdkConfHeart(HttpServletRequest request){
        String heartId = request.getParameter("heartId");
        JSONResult jsonResult = new JSONResult();
        try {
            int count = sdkService.deleteSdkConfHeartById(heartId);
            if(count > 0)
            {
                jsonResult.setSuccessInfo("删除性能配置信息成功！");
            }else
            {
                jsonResult.setFailInfo("删除性能配置信息失败！");
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("删除性能配置信息异常,请稍后再试！");
        }
        return jsonResult;
    }

    /**
     * 新增修改主机配置
     * @param request
     * @param sdkConfHost
     * @return
     */
    @RequestMapping(value = "saveSDKConfHost",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult saveSDKConfHost(HttpServletRequest request,SDKConfHost sdkConfHost){
        JSONResult jsonResult = new JSONResult();
        try
        {
            String hostId = sdkConfHost.getHostId();
            if (StringUtils.isEmpty(hostId))
            {
                sdkConfHost.setHostId(UUID.randomUUID().toString().replace("-",""));
                int count = sdkService.addSDKConfHost(sdkConfHost);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("新增节点黑名单成功！");
                }else
                {
                    jsonResult.setFailInfo("新增节点黑名单失败！");
                }
            }else
            {
                int count = sdkService.updateSDKConfHost(sdkConfHost);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("修改节点黑名单成功！");
                }else
                {
                    jsonResult.setFailInfo("修改节点黑名单失败！");
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("操作节点黑名单异常！");
        }
        return  jsonResult;
    }



    /**
     * 查看主机配置详情
     * @param request
     * @return
     */
    @RequestMapping(value = "SDKConfHostDetail",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult querySDKConfHostDetail(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String hostId = request.getParameter("hostId");
        try
        {
            SDKConfHost sdkConfHost = sdkService.querySDKConfHostDetail(hostId);
            jsonResult.setData(sdkConfHost);
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("查看主机配置信息异常！");
        }
        return  jsonResult;
    }

    /**
     * 删除主机配置详情
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteSDKConfHost",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteSDKConfHostByHostId(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String hostId = request.getParameter("hostId");
        try
        {
            int count = sdkService.deleteSDKConfHostByHostId(hostId);
            if (count > 0)
            {
                jsonResult.setSuccessInfo("删除主机配置信息成功！");
            }else
            {
                jsonResult.setFailInfo("删除主机配置信息失败！");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("删除主机配置信息异常！");
        }
        return  jsonResult;
    }



    /**
     * 新增修改微环境配置
     * @param request
     * @return
     */
    @RequestMapping(value = "saveSDKConfEnv",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult saveSDKConfEnv(HttpServletRequest request, SDKConfEnv sdkConfEnv){
        JSONResult jsonResult = new JSONResult();
        try
        {
            String envId = sdkConfEnv.getEnvId();
            if (StringUtils.isEmpty(envId))
            {
                sdkConfEnv.setEnvId(UUID.randomUUID().toString().replace("-",""));
                int count = sdkService.addSDKConfEnv(sdkConfEnv);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("新增微环境配置信息成功！");
                }else
                {
                    jsonResult.setFailInfo("新增微环境配置信息失败！");
                }
            }else
            {
                int count = sdkService.updateSDKConfEnv(sdkConfEnv);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("修改微环境配置信息成功！");
                }else
                {
                    jsonResult.setFailInfo("修改微环境配置信息失败！");
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("操作微环境配置信息异常！");
        }
        return  jsonResult;
    }



    /**
     * 查看微环境配置详情
     * @param request
     * @return
     */
    @RequestMapping(value = "SDKConfEnvDetail",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult querySDKConfEnvDetail(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String envId = request.getParameter("envId");
        try
        {
            Map<String,Object> result = new HashMap<String,Object>();
            SDKConfEnv sdkConfEnv = sdkService.querySDKConfEnvDetailByEnvId(envId);
            result.put("nodeCodes",sdkConfEnv.getNodeCodes());
            result.put("param",sdkConfEnv);
            jsonResult.setData(result);
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("查看微环境配置信息异常！");
        }
        return  jsonResult;
    }

    /**
     * 删除微环境配置详情
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteSDKConfEnv",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteSDKConfEnv(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String envId = request.getParameter("envId");
        try
        {
            int count = sdkService.deleteSDKConfEnvByEnvId(envId);
            if (count > 0)
            {
                jsonResult.setSuccessInfo("删除主机配置信息成功！");
            }else
            {
                jsonResult.setFailInfo("删除主机配置信息失败！");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("删除主机配置信息异常！");
        }
        return  jsonResult;
    }


    /**
     * 查询节点集合
     * @param request
     * @return
     */
    @RequestMapping(value ="sdkConfHostNodeList",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult sdkConfHostNodeList(HttpServletRequest request) throws IOException {
        JSONResult jsonResult = new JSONResult();
        String hostId = request.getParameter("hostId");
        List<SDKConfHostNode> sdkConfHostNodes = sdkService.querySDKConfHostNode(hostId);
        ObjectMapper objectMapper = new ObjectMapper();

        jsonResult.setData(sdkConfHostNodes);
        return jsonResult;
    }


    /**
     * 新增修改黑名单节点配置
     * @param request
     * @return
     */
    @RequestMapping(value = "saveSDKConfHostNode",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult saveSDKConfHostNode(HttpServletRequest request, SDKConfHostNode sdkConfHostNode){
        JSONResult jsonResult = new JSONResult();
        try
        {
            String nodeId = sdkConfHostNode.getNodeId();
            if (StringUtils.isEmpty(nodeId))
            {
                sdkConfHostNode.setNodeId(UUID.randomUUID().toString().replace("-",""));
                int count = sdkService.addSDKConfHostNode(sdkConfHostNode);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("新增节点配置信息成功！");
                }else
                {
                    jsonResult.setFailInfo("新增节点配置信息失败！");
                }
            }else
            {
                int count = sdkService.updateSDKConfHostNode(sdkConfHostNode);
                if (count > 0)
                {
                    jsonResult.setSuccessInfo("修改节点配置信息成功！");
                }else
                {
                    jsonResult.setFailInfo("修改节点配置信息失败！");
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("操作节点配置信息异常！");
        }
        return  jsonResult;
    }



    /**
     * 查看黑名单节点配置详情
     * @param request
     * @return
     */
    @RequestMapping(value = "SDKConfHostNodeDetail",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult querySDKConfHostNodeDetail(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String nodeId = request.getParameter("nodeId");
        try
        {
            SDKConfHostNode sdkConfHostNode = sdkService.querySDKConfHostNodeDetail(nodeId);
            jsonResult.setData(sdkConfHostNode);
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("查看节点配置信息异常！");
        }
        return  jsonResult;
    }

    /**
     * 删除节点配置详情
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteSDKConfHostNode",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteSDKConfHostNode(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String nodeId = request.getParameter("nodeId");
        try
        {
            int count = sdkService.deleteSDKConfHostNodeByNodeId(nodeId);
            if (count > 0)
            {
                jsonResult.setSuccessInfo("删除节点配置信息成功！");
            }else
            {
                jsonResult.setFailInfo("删除节点配置信息失败！");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("删除节点配置信息异常！");
        }
        return  jsonResult;
    }


}
