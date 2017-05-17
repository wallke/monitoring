package com.xwtech.omweb.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.Room;

import java.util.List;

/**
 * Created by wangyu on 2017/2/10.
 */
public interface IRoomService {

    List<Room> getRooms(Room room, PageInfo pageInfo);
    List<Room> getAllRooms();
    
    List<Room> getRoomsByAppGroupId(String id);

    Room getRoomById(String id);
    Room getRoomByNum(String num);
    Room getRoomByName(String name);
    Room getRoomByNumExceptSelf(Room room);
    Room getRoomByNameExceptSelf(Room room);

    boolean createRoom(Room room);
    boolean updateRoom(Room room);
    boolean deleteRoom(String id);
    /**
     * 根据机房ID查询机房下是否有主机
     * @param id
     * @return
     */
    int queryRoomNextServer(String id);
}
