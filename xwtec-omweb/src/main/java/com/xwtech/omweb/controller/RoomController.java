package com.xwtech.omweb.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.service.IRoomService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.xwtech.omweb.model.Room;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * Created by wangyu on 2017/2/9.
 */
@RestController
@RequestMapping("omweb/room")
public class RoomController {

    @Autowired
    IRoomService roomService;

    @RequestMapping("index")
    public ModelAndView index(@RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn,
                              String num, String name) {

        PageInfo<Room> pageInfo = new PageInfo<Room>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);

        Room room = new Room();
        room.setNum(num);
        room.setName(name);
        List<Room> list = roomService.getRooms(room, pageInfo);

        ModelAndView modelAndView = new ModelAndView("room/index").addObject("room", room).addObject("roomList", list).addObject("pageInfo",
                ((Page<Room>) list).toPageInfo());

        return modelAndView;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    private ModelAndView create(String roomId) {
        Room room = new Room();
        if (StringUtils.isNotEmpty(roomId)) {
            room = roomService.getRoomById(roomId);
        }
        ModelAndView modelAndView = new ModelAndView("room/create").addObject("room", room);
        return modelAndView;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    private JSONResult createSubmit(HttpServletRequest request, String roomId, String num,
                                    String name, int enabled, String desc, String memo) {
        JSONResult jsonResult = new JSONResult();
        try {
            User user = (User)request.getAttribute(Constants.CURRENT_USER);

            //新增
            if (roomId == null || roomId.equals("")) {
                Room room = roomService.getRoomByNum(num);
                if (room != null) {
                    jsonResult.setFailInfo("编码已存在，无法新增！");
                    return jsonResult;
                }
                room = roomService.getRoomByName(name);
                if (room != null) {
                    jsonResult.setFailInfo("名称已存在，无法新增！");
                    return jsonResult;
                }

                room = new Room();
                room.setId(UUID.randomUUID().toString().replaceAll("-",""));
                room.setNum(num);
                room.setName(name);
                room.setIsUse(1);
                room.setIsEnabled(enabled);
                room.setDesc(desc);
                room.setMemo(memo);
                room.setCreateManCode(String.valueOf(user.getId()));
                room.setCreateManName(user.getName());
                room.setCreateTime(user.getCreateTime());
                room.setCreateMemo("");
                room.setLastManCode(String.valueOf(user.getId()));
                room.setLastManName(user.getName());
                room.setLastTime(user.getCreateTime());
                room.setLastMemo("");
                roomService.createRoom(room);
                jsonResult.setSuccessInfo("新增机房成功！");
            }
            //修改
            else {
                Room paramRoom = new Room();
                paramRoom.setId(roomId);
                paramRoom.setNum(num);
                paramRoom.setName(name);
                //查询除本身id以外，是否存在相同编码的机房
                Room room = roomService.getRoomByNumExceptSelf(paramRoom);
                if (room != null) {
                    jsonResult.setFailInfo("编码已存在，修改失败！");
                    return jsonResult;
                }
                //查询除本身id以外，是否存在相同名称的机房
                room = roomService.getRoomByNameExceptSelf(paramRoom);
                if (room != null) {
                    jsonResult.setFailInfo("名称已存在，修改失败！");
                    return jsonResult;
                }

                room = roomService.getRoomById(roomId);
                if (room == null) {
                    jsonResult.setFailInfo("机房不存在，修改失败！");
                    return jsonResult;
                }

                room.setNum(num);
                room.setName(name);
                room.setIsEnabled(enabled);
                room.setDesc(desc);
                room.setMemo(memo);
                room.setLastManCode(String.valueOf(user.getId()));
                room.setLastManName(user.getName());
                room.setLastTime(user.getCreateTime());
                roomService.updateRoom(room);
                jsonResult.setSuccessInfo("修改机房成功！");
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
            //查看机房下面是否有主机，如有，不允许删除
            int count = roomService.queryRoomNextServer(id);
            if (count > 0)
            {
                jsonResult.setFailInfo("机房下存在主机，删除失败！");
                return jsonResult;
            }
            roomService.deleteRoom(id);
            jsonResult.setSuccessInfo("删除机房成功！");
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
        }
        return jsonResult;
    }
}
