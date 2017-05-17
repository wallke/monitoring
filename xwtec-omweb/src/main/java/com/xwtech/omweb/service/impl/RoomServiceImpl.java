package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.RoomMapper;
import com.xwtech.omweb.model.Room;
import com.xwtech.omweb.service.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangyu on 2017/2/10.
 */
@Service
public class RoomServiceImpl implements IRoomService {

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public List<Room> getRooms(Room room, PageInfo pageInfo) {
        if (pageInfo != null) {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        return roomMapper.getRooms(room);
    }
    
    @Override
	public List<Room> getRoomsByAppGroupId(String id) {
		return roomMapper.getRoomsByAppGroupId(id);
	}

    @Override
    public List<Room> getAllRooms() {
        return roomMapper.getAllRooms();
    }

    @Override
    public Room getRoomById(String id) {
        return roomMapper.getRoomById(id);
    }

    @Override
    public Room getRoomByNum(String num) {
        return roomMapper.getRoomByNum(num);
    }

    @Override
    public Room getRoomByName(String name) {
        return roomMapper.getRoomByName(name);
    }

    @Override
    public Room getRoomByNumExceptSelf(Room room) {
        return roomMapper.getRoomByNumExceptSelf(room);
    }

    @Override
    public Room getRoomByNameExceptSelf(Room room) {
        return roomMapper.getRoomByNameExceptSelf(room);
    }

    @Override
    public boolean createRoom(Room room) {
        return roomMapper.createRoom(room) > 0;
    }

    @Override
    public boolean updateRoom(Room room) {
        return roomMapper.updateRoom(room) > 0;
    }

    @Override
    public boolean deleteRoom(String id) {
        return roomMapper.deleteRoom(id) > 0;
    }

    /**
     * 根据机房ID查询机房下是否有主机
     *
     * @param id
     * @return
     */
    @Override
    public int queryRoomNextServer(String id) {
        return roomMapper.queryRoomNextServer(id);
    }


}
