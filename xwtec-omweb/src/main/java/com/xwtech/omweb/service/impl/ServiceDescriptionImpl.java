package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.ServiceDescriptionMapper;
import com.xwtech.omweb.model.ServiceDescription;
import com.xwtech.omweb.service.IServiceDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by zhangq on 2017/3/28.
 */
@Service
public class ServiceDescriptionImpl implements IServiceDescription {

    @Autowired
    ServiceDescriptionMapper serviceDescriptionMapper;

    @Override
    public int add(ServiceDescription serviceDescription) {

        serviceDescription.setId(UUID.randomUUID().toString().replaceAll("-",""));

        return serviceDescriptionMapper.add(serviceDescription);
    }

    @Override
    public int delete(String id) {
        return serviceDescriptionMapper.delete(id);
    }

    @Override
    public ServiceDescription get(String service) {
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setService(service);
        return serviceDescriptionMapper.get(serviceDescription);
    }

    @Override
    public List<ServiceDescription> getAll(ServiceDescription serviceDescription, PageInfo pageInfo) {

        if (pageInfo != null) {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }

        return serviceDescriptionMapper.getAll(serviceDescription);
    }
}
