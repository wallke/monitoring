package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.ServiceDescription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/2/15 0015.
 * 应用节点
 */
@Mapper
public interface ServiceDescriptionMapper {

    /**
     *
     * @param serviceDescription
     * @return
     */
    int add(ServiceDescription serviceDescription);


    /**
     *
     * @param id
     * @return
     */
    int delete(@Param("id") String id);

    /**
     *
     * @param serviceDescription
     * @return
     */
    ServiceDescription get(ServiceDescription serviceDescription);


    List<ServiceDescription> getAll(ServiceDescription serviceDescription);

}
