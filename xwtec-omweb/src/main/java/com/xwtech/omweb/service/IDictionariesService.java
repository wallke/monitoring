package com.xwtech.omweb.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.Dictionaries;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
public interface IDictionariesService {

    List<Dictionaries> queryDictionariesList(PageInfo pageInfo);

    /**
     * 根据级别修改级别颜色
     * @return
     */
    int updateDictionaries(Dictionaries dictionaries);
}
