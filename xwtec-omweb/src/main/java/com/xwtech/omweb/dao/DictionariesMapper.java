package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.Dictionaries;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
@Mapper
public interface DictionariesMapper {

    List<Dictionaries> queryDictionariesList();

    /**
     * 根据级别修改级别颜色
     * @return
     */
    int updateDictionaries(Dictionaries dictionaries);
}
