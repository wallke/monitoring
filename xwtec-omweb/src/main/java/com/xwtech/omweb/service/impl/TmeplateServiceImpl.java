package com.xwtech.omweb.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.service.AlarmService;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.dao.TemplateMapper;
import com.xwtech.omweb.model.AlarmScheme;
import com.xwtech.omweb.model.Template;
import com.xwtech.omweb.model.TemplateChannel;
import com.xwtech.omweb.service.TmeplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
@Service
public class TmeplateServiceImpl implements TmeplateService {

    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ElasticConfig elasticConfig;

    @Override
    @Transactional
    public int addTemplate(Template template,String[] channels, String[] message_templates) throws JsonProcessingException {
        List<TemplateChannel> TemplateChannels = new ArrayList<TemplateChannel>();
        if (channels!=null && channels.length > 0)
        {
            for (int i =0; i<channels.length;i++)
            {
                TemplateChannel templateChannel = new TemplateChannel();
                templateChannel.setChannel(channels[i]);
                templateChannel.setMessage_template(message_templates[i]);
                TemplateChannels.add(templateChannel);
                templateChannel.setTemplate_channel_id(UUID.randomUUID().toString().replace("-",""));
                templateChannel.setTemplate_id(template.getTemplate_id());
                templateMapper.addTemplateChannel(templateChannel);
            }
            template.setTemplates(TemplateChannels);
        }
        //推送ES
        int count = templateMapper.addTemplate(template);
        if (count > 0)
        {
            count = PostParamsEs(template);
        }

        return count;
    }


    @Transactional
    public int PostParamsEs(Template template) throws JsonProcessingException {
        int count = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = objectMapper.writeValueAsString(template);
        int status = alarmService.postCommonEs(json, elasticConfig.getTemplateType(), template.getName());
        if (status  >= 200 && status <300)
        {
            count = 1;
        }else
        {
            count = 0;
            throw new RuntimeException("应用失败");
        }
        return count;
    }


    @Override
    public int updateTemplate(Template template, String[] channels, String[] message_templates) throws JsonProcessingException {
        JSONResult jsonResult = new JSONResult();
        List<TemplateChannel> TemplateChannels = new ArrayList<TemplateChannel>();
        if (channels!=null && channels.length > 0)
        {
            for (int i =0; i<channels.length;i++)
            {
                TemplateChannel templateChannel = new TemplateChannel();
                templateChannel.setChannel(channels[i]);
                templateChannel.setMessage_template(message_templates[i]);
                TemplateChannels.add(templateChannel);
                templateChannel.setTemplate_id(template.getTemplate_id());
                templateMapper.updateTemplateChannel(templateChannel);
            }
            template.setTemplates(TemplateChannels);
        }
        //推送ES
        int count = templateMapper.updateTemplate(template);
        if (count > 0)
        {
            count = PostParamsEs(template);
            jsonResult.setSuccessInfo("修改模板成功");
        }

        return count;
    }


    @Override
    public Template queryTemplateDetail(String template_id) {
        Template template = templateMapper.queryTemplateDetail(template_id);
        return template;
    }

    @Override
    public List<Template> queryTemplateList(PageInfo pageInfo) {
        if (pageInfo !=null)
        {
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return templateMapper.queryTemplateList();
    }


    @Override
    @Transactional
    public int deleteTemplate(String template_id) {
        Template template = templateMapper.queryTemplateDetail(template_id);
        templateMapper.deleteTemplateChannel(template_id);
        int count = templateMapper.deleteTemplateById(template_id);

        if (count > 0)
        {
            int status = alarmService.deleteCommonEs(elasticConfig.getTemplateType(),template.getName());
            if (status  >= 200 && status <300)
            {
                count = 1;
            }else
            {
                count = 0;
                throw new RuntimeException("应用失败");
            }
        }
        return count ;
    }

}
