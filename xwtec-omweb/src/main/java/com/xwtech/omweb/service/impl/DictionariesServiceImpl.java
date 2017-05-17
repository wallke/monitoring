package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.DictionariesMapper;
import com.xwtech.omweb.model.Dictionaries;
import com.xwtech.omweb.service.IDictionariesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
@Service
public class DictionariesServiceImpl implements IDictionariesService {

    @Autowired
    private DictionariesMapper dictionariesMapper;

    @Override
    public List<Dictionaries> queryDictionariesList(PageInfo pageInfo) {
        if (pageInfo !=null)
        {
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return dictionariesMapper.queryDictionariesList();
    }

    /**
     * 根据级别修改级别颜色
     *
     * @param dictionaries
     * @return
     */
    @Override
    public int updateDictionaries(Dictionaries dictionaries) {
        return dictionariesMapper.updateDictionaries(dictionaries);
    }

}
