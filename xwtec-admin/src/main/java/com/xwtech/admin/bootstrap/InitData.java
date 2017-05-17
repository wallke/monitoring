package com.xwtech.admin.bootstrap;

import com.xwtech.admin.InternetProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import xwtec.servercm.ServerCM;
import xwtec.servercm.store.LogStore;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 埋点sdk初始化
 * Created by zq on 16/9/13.
 */
@Component
public class InitData implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitData.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //jar 包内根路径资源文件
        ServerCM.setYamlPath(this.getClass().getResource("/").toURI().getPath());
        ServerCM.setLogStore(new StoreToLog4J());
        ServerCM.open("192.168.1.252", "8080");
    }


    private class StoreToLog4J implements LogStore {

        Logger logger = LoggerFactory.getLogger("XWTEC_ServerCMLogger");

        @Override
        public void saveToFile(String s) {
            logger.info(s);
        }

    }

}
