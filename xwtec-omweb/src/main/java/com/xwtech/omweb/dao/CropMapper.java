package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.Crop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/2/15 0015.
 * 公司信息
 */
@Mapper
public interface CropMapper {

    /**
     * 查询所有公司信息
     * @return
     */
    List<Crop> queryCorpList(@Param("cropName")String cropName);

    /**
     * 查询所有公司信息
     * @return
     */
    List<Crop> getCorpList();

    /**
     * 新增公司信息
     * @return
     */
    int addCorp(Crop crop);

    /**
     * 修改公司信息
     * @return
     */
    int updateCrop(Crop crop);

    /**
     * 删除公司信息
     * @param cropId
     * @return
     */
    int deleteCrop(@Param("cropId")String cropId);

    /**
     * 根据ID查询公司信息
     * @param cropId
     * @return
     */
    Crop queryCropById(@Param("cropId")String cropId);

    /**
     * 查询公司名称是否存在
     * @param cropId
     * @param cropName
     * @return
     */
    int queryCropNameIsExist(@Param("cropId")String cropId,@Param("cropName")String cropName);

}
