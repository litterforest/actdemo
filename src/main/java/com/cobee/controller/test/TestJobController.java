package com.cobee.controller.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test/job")
public class TestJobController {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 使用serviceTask元素来实现异步执行工作
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/asyncJob", method = RequestMethod.GET)
    @ResponseBody
    public String asyncJob() throws Exception
    {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/serviceTask.bpmn");
        Deployment deployment = deploymentBuilder.deploy();

        // 查找流程定义对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(deployment.getId());
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        Thread.sleep(1000);
        System.out.println("processInstance id:" + processInstance.getId());
        return "success";
    }

}
