package com.xwtech.omweb.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.ServiceDescription;

import java.util.List;

/**
 * Created by zhangq on 2017/3/28.
 */
public interface IServiceDescription {

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
    int delete(String id);

    /**
     *
     * @param service
     * @return
     */
    ServiceDescription get(String service);


    List<ServiceDescription> getAll(ServiceDescription serviceDescription, PageInfo pageInfo);


}
