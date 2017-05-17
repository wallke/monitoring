/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.init;

import java.io.Serializable;

/**
 * Elasticsearch文档,日志控制
 *
 * @author 张岩松
 */
public class ESLogDocument implements Serializable {

    private String _index;
    private String _type;
    private String _id;
    private String _version;
    private String found;
    private ConfigeInfo _source;

    public String get_index() {
        return _index;
    }

    public void set_index(String _index) {
        this._index = _index;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_version() {
        return _version;
    }

    public void set_version(String _version) {
        this._version = _version;
    }

    public String getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = found;
    }

    public ConfigeInfo get_source() {
        return _source;
    }

    public void set_source(ConfigeInfo _source) {
        this._source = _source;
    }

}
