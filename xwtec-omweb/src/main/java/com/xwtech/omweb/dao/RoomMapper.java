package com.xwtech.omweb.dao;

import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.Room;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by wangyu on 2017/2/10.
 */
@Mapper
public interface RoomMapper {

    List<Room> getRooms(Room room);
    List<Room> getAllRooms();

    List<Room> getRoomsByAppGroupId(String id);
    
    Room getRoomById(String id);
    Room getRoomByNum(String num);
    Room getRoomByName(String name);
    Room getRoomByNumExceptSelf(Room room);
    Room getRoomByNameExceptSelf(Room room);

    int createRoom(Room room);
    int updateRoom(Room room);
    int deleteRoom(String id);

    /**
     * 根据机房ID查询机房下是否有主机
     * @param id
     * @return
     */
    int queryRoomNextServer(String id);
}
