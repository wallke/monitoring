package com.xwtech.omweb.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.App;
import com.xwtech.omweb.model.Node;
import com.xwtech.omweb.model.Server;
import com.xwtech.omweb.service.IAppService;
import com.xwtech.omweb.service.INodeService;
import com.xwtech.omweb.service.IServerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by wangyu on 2017/2/15.
 */
@RestController
@RequestMapping("omweb/node")
public class NodeController {

    @Autowired
    INodeService nodeService;

    @Autowired
    IServerService serverService;

    @Autowired
    IAppService appService;

    @RequestMapping("index")
    public ModelAndView index(@RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn,
                              String num, String name,
                              String serverId, String port, String appId) {

        PageInfo<Node> pageInfo = new PageInfo<Node>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);

        Node node = new Node();
        node.setNum(num);
        node.setName(name);
        node.setServerId(serverId);
        node.setPort(port);
        node.setAppId(appId);

        List<Node> nodeList = nodeService.getNodes(node, pageInfo);

        List<Server> serverList = serverService.queryServerList();
        if (serverList == null) {
            serverList = new ArrayList<Server>();
        }
        Server server = new Server();
        server.setHostName("请选择");
        serverList.add(0, server);

        List<App> appList = appService.getAppList();
        if (appList == null) {
            appList = new ArrayList<App>();
        }
        App app = new App();
        app.setAppName("请选择");
        appList.add(0, app);

        ModelAndView modelAndView = new ModelAndView("node/index").
                addObject("node", node).
                addObject("nodeList", nodeList).
                addObject("serverList", serverList).
                addObject("appList", appList).
                addObject("pageInfo", ((Page<Node>) nodeList).toPageInfo());
        return modelAndView;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    private ModelAndView create(String id) {
        Node node = new Node();
        if (StringUtils.isNotEmpty(id)) {
            node = nodeService.getNodeById(id);
        }
        List<Server> serverList = serverService.queryServerList();
        List<App> appList = appService.getAppList();

        ModelAndView modelAndView = new ModelAndView("node/create").
                addObject("node", node).
                addObject("serverList", serverList).
                addObject("appList", appList);
        return modelAndView;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    private JSONResult createSubmit(HttpServletRequest request, String id, String num,
                                    String name, String serverId, String port, String appId,
                                    int enabled, String desc, String memo) {
        JSONResult jsonResult = new JSONResult();
        try {
            User user = (User)request.getAttribute(Constants.CURRENT_USER);

            //新增
            if (id == null || id.equals("")) {
                Node node = new Node();
                node.setId(UUID.randomUUID().toString().replaceAll("-",""));
                node.setNum(UUID.randomUUID().toString().replaceAll("-",""));
                node.setName(UUID.randomUUID().toString().replaceAll("-",""));
                node.setServerId(serverId);
                node.setPort(port);
                node.setAppId(appId);
                node.setIsUse(1);
                node.setIsEnabled(enabled);
                node.setDesc(desc);
                node.setMemo(memo);
                node.setCreateManCode(String.valueOf(user.getId()));
                node.setCreateManName(user.getName());
                node.setCreateTime(user.getCreateTime());
                node.setCreateMemo("");
                node.setLastManCode(String.valueOf(user.getId()));
                node.setLastManName(user.getName());
                node.setLastTime(user.getCreateTime());
                node.setLastMemo("");
                nodeService.createNode(node);
                jsonResult.setSuccessInfo("新增节点成功！");
            }
            //修改
            else {
                Node paramNode = new Node();
                paramNode.setId(id);
                Node  node = nodeService.getNodeById(id);
                if (node == null) {
                    jsonResult.setFailInfo("节点不存在，修改失败！");
                    return jsonResult;
                }
                node.setServerId(serverId);
                node.setPort(port);
                node.setAppId(appId);
                node.setIsEnabled(enabled);
                node.setDesc(desc);
                node.setMemo(memo);
                node.setLastManCode(String.valueOf(user.getId()));
                node.setLastManName(user.getName());
                node.setLastTime(user.getCreateTime());
                nodeService.updateNode(node);
                jsonResult.setSuccessInfo("修改节点成功！");
            }
            return jsonResult;

        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
            return jsonResult;
        }
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    private JSONResult deleteSubmit(String id) {
        JSONResult jsonResult = new JSONResult();
        try {
            nodeService.deleteNode(id);
            jsonResult.setSuccessInfo("删除节点成功！");
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
        }
        return jsonResult;
    }
}
