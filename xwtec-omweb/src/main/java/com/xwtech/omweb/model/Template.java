package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
public class Template {

    @JsonIgnore
    private String template_id;

    //模板名称
    @JsonProperty("template_name")
    private String name;
    @JsonIgnore
    private String channel;
    @JsonIgnore
    private String message_template;

    private  List<TemplateChannel> templates;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage_template() {
        return message_template;
    }

    public void setMessage_template(String message_template) {
        this.message_template = message_template;
    }



    public List<TemplateChannel> getTemplates() {
        return templates;
    }

    public void setTemplates(List<TemplateChannel> templates) {
        this.templates = templates;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


