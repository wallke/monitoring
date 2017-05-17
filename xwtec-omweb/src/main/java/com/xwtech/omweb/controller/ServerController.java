package com.xwtech.omweb.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.Node;
import com.xwtech.omweb.model.Room;
import com.xwtech.omweb.model.Server;
import com.xwtech.omweb.model.ServerInfo;
import com.xwtech.omweb.service.INodeService;
import com.xwtech.omweb.service.IRoomService;
import com.xwtech.omweb.service.IServerService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by zhongxueye on 17-2-10.
 * 主机管理控制层
 */
@Controller
@RequestMapping("omweb/server")
public class ServerController {

    private final static Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Resource(name = "serverService")
    private IServerService serverService;

    @Autowired
    private IRoomService sysService;

    @Autowired
    private INodeService nodeService;

    @RequestMapping(value = "index",method ={ RequestMethod.GET,RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request,
                              String name, String loginName, @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn){
        String hostNum = request.getParameter("hostNum");
        String hostName = request.getParameter("hostName");
        String roomName = request.getParameter("roomName");
        String type = request.getParameter("type");
        PageInfo<Server> pageInfo = new PageInfo<Server>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<Room> roomList = sysService.getAllRooms();

        List<Server> serverList = serverService.queryServerListInfo(hostNum, hostName, type,roomName,pageInfo);
        return new ModelAndView("server/index").addObject("serverList", serverList).addObject("hostNum", hostNum)
                .addObject("hostName",hostName).addObject("type",type).addObject("roomName",roomName)
                .addObject("roomList",roomList)
                .addObject("pageInfo",((Page<Server>) serverList).toPageInfo());
    }

    /**
     * 跳转主机新增修改页面
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String serverId){
        String actionType = "create";

        Server server = new Server();
        ServerInfo serverInfo = new ServerInfo();
        server.setServerInfo(serverInfo);
        List<Room> roomList = sysService.getAllRooms();

        if (StringUtils.isNotEmpty(serverId)) {
            actionType= "modfiy";
            //根据id查看主机详情
            server = serverService.queryServerDetail(serverId);
        }
        return new ModelAndView("server/create").addObject("actionType",actionType).addObject("server", server).addObject("roomList",roomList);

    }

    @RequestMapping(value = "serverDetail",method = RequestMethod.GET)
    public ModelAndView serverDetail(String serverId){
        Server  server = serverService.queryServerDetail(serverId);
        return new ModelAndView("server/detail").addObject("server",server);

    }

    /**
     * 新增主机信息或修改主机信息
     * @param request
     * @param server
     * @return
     */
    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute Server server){
        String actionType = request.getParameter("actionType");
        JSONResult jsonResult = new JSONResult();
        User user = (User)request.getAttribute(Constants.CURRENT_USER);
        if (user != null )
        {
            server.setCreateManCode(String.valueOf(user.getId()));
            server.setCreateManName(user.getLoginName());
            server.setLastManCode(String.valueOf(user.getId()));
            server.setLastManName(user.getLoginName());
            server.setIpAddress(request.getLocalAddr());
            server.setCreateMemo("");
            server.setLastMemo("");
        }
        int count = serverService.queryServerNum(server.getHostNum(), StringUtil.isNotEmpty(server.getServerId())?server.getServerId():null);
        if(count >0)
        {
            jsonResult.setFailInfo("主机IP已经存在，请修正");
            return  jsonResult;
        }
        count = serverService.queryServerName(server.getHostName(), StringUtil.isNotEmpty(server.getServerId())?server.getServerId():null);
        if(count > 0)
        {
            jsonResult.setFailInfo("主机名称已经存在，请修正");
            return  jsonResult;
        }
        try
        {
            if ("create".equals(actionType))
            {
                //新增
                boolean bool = serverService.insertServer(server);
                if(bool)
                {
                    jsonResult.setSuccessInfo("新增主机成功！");
                }else
                {
                    jsonResult.setFailInfo("新增主机失败，请稍后再试！");
                }
            }else if ("modfiy".equals(actionType))
            {
                //修改
                boolean bool = serverService.updateServer(server);
                if(bool)
                {
                    jsonResult.setSuccessInfo("修改主机成功！");
                }else
                {
                    jsonResult.setFailInfo("修改主机失败，请稍后再试！");
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
     * 根据主机ID删除主机
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteServer",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteServer(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String serverId = request.getParameter("serverId");
        Node node = new Node();

        if (serverId !=null && !serverId.equals(""))
        {
            node.setServerId(serverId);
            List<Node> nodes = nodeService.getNodes(node, null);
            if(nodes !=null && nodes.size() >0)
            {
                jsonResult.setFailInfo("该主机下面存在节点，删除主机失败！");
                return jsonResult;
            }
            try {
                int count = serverService.deleteServerById(serverId);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除主机成功！");
                }else
                {
                    jsonResult.setFailInfo("删除主机失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除主机异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除主机参数异常，请稍后再试！");
        }

        return jsonResult;
    }



}
