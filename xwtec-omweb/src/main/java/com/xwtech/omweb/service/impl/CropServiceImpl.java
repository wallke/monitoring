package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.CropMapper;
import com.xwtech.omweb.model.Crop;
import com.xwtech.omweb.service.ICropService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
@Service("cropService")
public class CropServiceImpl  implements ICropService {

    @Autowired
    private CropMapper cropMapper;

    /**
     * 查询所有公司信息
     *
     * @param cropName
     * @return
     */
    @Override
    public List<Crop> queryCorpList(@Param("cropName") String cropName,PageInfo pageInfo) {
        if(pageInfo !=null)
        {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        return cropMapper.queryCorpList(cropName);
    }

    /**
     * 查询所有公司信息
     *
     * @return
     */
    @Override
    public List<Crop> getCorpList() {
        return cropMapper.getCorpList();
    }

    /**
     * 新增公司信息
     *
     * @return
     */
    @Override
    public int addCorp(Crop crop) {
        crop.setCropId(UUID.randomUUID().toString().replace("-",""));
        return cropMapper.addCorp(crop);
    }

    /**
     * 修改公司信息
     *
     * @return
     */
    @Override
    public int updateCrop(Crop crop) {
        return cropMapper.updateCrop(crop);
    }

    /**
     * 删除公司信息
     *
     * @param cropId
     * @return
     */
    @Override
    public int deleteCrop(@Param("cropId") String cropId) {
        return cropMapper.deleteCrop(cropId);
    }

    /**
     * 根据ID查询公司信息
     *
     * @param cropId
     * @return
     */
    @Override
    public Crop queryCropById(@Param("cropId") String cropId) {
        return cropMapper.queryCropById(cropId);
    }

    /**
     * 查询公司名称是否存在
     *
     * @param cropId
     * @param cropName
     * @return
     */
    @Override
    public int queryCropNameIsExist(@Param("cropId") String cropId, @Param("cropName") String cropName) {
        return cropMapper.queryCropNameIsExist(cropId,cropName);
    }
}
