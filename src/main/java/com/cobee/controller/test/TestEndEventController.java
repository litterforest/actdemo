package com.cobee.controller.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 结束事件，一般都是抛出型事件
 */
@Controller
@RequestMapping("/test/EndEvent")
public class TestEndEventController {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;

    /**
     * 使用错误码来起动一子流程，错误开始事件只能出现在子流程容器里面。
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/errorEndEvent", method = RequestMethod.GET)
    @ResponseBody
    public String errorEndEvent() throws Exception
    {
        // 部署后系统自动按时间规则来起动流程实例
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/endEvent/errorEndEvent.bpmn");
        Deployment deployment = deploymentBuilder.deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        //System.out.println("processInstance id:" + processInstance.getId());
        return "success";
    }

}
