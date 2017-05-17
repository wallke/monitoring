package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.Template;
import com.xwtech.omweb.model.TemplateChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
@Mapper
public interface TemplateMapper {

    int addTemplate(Template template);

    int addTemplateChannel(TemplateChannel templateChannel);

    Template queryTemplateDetail(@Param("template_id") String template_id);

    List<Template> queryTemplateList();

    List<TemplateChannel> queryTemplateChannelList(String template_id);

    int updateTemplate(Template template);

    int updateTemplateChannel(TemplateChannel templateChannel);

    int deleteTemplateById(String template_id);

    int deleteTemplateChannel(String template_id);

}
