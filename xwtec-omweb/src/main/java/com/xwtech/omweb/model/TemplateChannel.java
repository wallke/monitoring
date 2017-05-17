package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Administrator on 2017/3/8 0008.
 */
public class TemplateChannel {
    @JsonIgnore
    private String template_id;
    @JsonIgnore
    private String template_channel_id;
    private String channel;
    private String message_template;

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTemplate_channel_id() {
        return template_channel_id;
    }

    public void setTemplate_channel_id(String template_channel_id) {
        this.template_channel_id = template_channel_id;
    }

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
}
