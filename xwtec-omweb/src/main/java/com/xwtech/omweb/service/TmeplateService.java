package com.xwtech.omweb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.Template;

import java.util.List;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
public interface TmeplateService {

    int addTemplate(Template template, String[] channels, String[] message_templates ) throws JsonProcessingException;

    Template queryTemplateDetail(String template_id);

    List<Template> queryTemplateList(PageInfo pageInfo);

    int updateTemplate(Template template, String[] channels, String[] message_templates) throws JsonProcessingException;

    int deleteTemplate(String template_id);

}
