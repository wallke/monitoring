/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.init;

import java.io.Serializable;
import java.util.List;

/**
 * 更新配置
 *
 * @author 张岩松
 */
public class UpdateInfo implements Serializable {

    private List<Update> updates;

    public List<Update> getUpdates() {
        return updates;
    }

    public void setUpdates(List<Update> updates) {
        this.updates = updates;
    }

    /**
     * 获得指定版本号的更新时间
     *
     * @param log_ver
     * @return
     */
    public String getUpdateTime(String log_ver) {
        for (Update update : this.updates) {
            if (update.log_ver.equals(log_ver)) {
                return update.update_time;
            }
        }
        return null;
    }

    /**
     * 版本更新
     */
    public static class Update implements Serializable {

        //更新的版本
        private String log_ver;
        //更新的时间
        private String update_time;

        public String getLog_ver() {
            return log_ver;
        }

        public void setLog_ver(String log_ver) {
            this.log_ver = log_ver;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

    }
}
