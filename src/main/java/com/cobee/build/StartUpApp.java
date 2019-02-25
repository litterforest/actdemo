package com.cobee.build;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;

public class StartUpApp {

    public static void main(String[] args) {
        // 起动工作流引擎，让框架自动创建表结构
        //ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("actprops/activiti.cfg.xml");
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        processEngine.close();
    }

}
