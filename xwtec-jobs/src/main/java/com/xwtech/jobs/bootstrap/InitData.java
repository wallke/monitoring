package com.xwtech.jobs.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import xwtec.servercm.ServerCM;
import xwtec.servercm.store.LogStore;

/**
 * Created by zq on 16/9/13.
 */
@Component
public class InitData implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitData.class);


    @Override
    public void run(ApplicationArguments  arguments) throws Exception {

//        Set<Class<?>> sets = ClassUtil.getClasses("com.xwtech.jobs.tasks");
//        for (Class class1:sets){
//            logger.debug("class2:{}",class1.getName());
//
//        }
//
        ServerCM.setYamlPath(this.getClass().getResource("/").toURI().getPath());
        ServerCM.setLogStore(new StoreToLog4J());
        ServerCM.open("192.168.40.64", "8080");
    }


    private class StoreToLog4J implements LogStore {

        Logger logger = LoggerFactory.getLogger("XWTEC_ServerCMLogger");

        @Override
        public void saveToFile(String s) {
            logger.info(s);
        }

    }

}
