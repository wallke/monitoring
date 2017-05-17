package com.xwtech.omweb.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.Dictionaries;
import com.xwtech.omweb.service.IDictionariesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
@Controller
@RequestMapping("omweb/dictionaries")
public class DictionariesController {

    @Autowired
    private IDictionariesService dictionariesService;

    @RequestMapping(value = "index", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request, String appNum, String appName,
                              String categoryId, String cropId,
                              @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn) {

        PageInfo<Dictionaries> pageInfo = new PageInfo<Dictionaries>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<Dictionaries> dictionariesList = dictionariesService.queryDictionariesList(pageInfo);

        return new ModelAndView("dictionaries/index").addObject("dictionariesList", dictionariesList)
                .addObject("pageInfo", ((Page<Dictionaries>) dictionariesList).toPageInfo());
    }

    @RequestMapping(value = "update",method =RequestMethod.POST )
    @ResponseBody
    public JSONResult update(Dictionaries dictionaries){

        JSONResult jsonResult = new JSONResult();
        try {
            int count = dictionariesService.updateDictionaries(dictionaries);
            if (count > 0)
            {
                jsonResult.setSuccessInfo("修改基础字典配置成功");
            }else
            {
                jsonResult.setFailInfo("修改基础字典配置失败");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("修改基础字典配置异常");
        }
        return jsonResult;
    }


}
