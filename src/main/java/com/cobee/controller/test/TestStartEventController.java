package com.cobee.controller.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试各种开始事件类型
 */
@Controller
@RequestMapping("/test/StartEvent")
public class TestStartEventController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;

    /**
     * 使用cron表达式来定义流程的启动
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/timerStartEvent", method = RequestMethod.GET)
    @ResponseBody
    public String timerStartEvent() throws Exception
    {
        // 部署后系统自动按时间规则来起动流程实例
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/startEvent/timerStartEvent.bpmn");
        Deployment deployment = deploymentBuilder.deploy();
        long counts = runtimeService.createProcessInstanceQuery().count();
        System.out.println("sleep前流程实例数:" + counts);
        Thread.sleep(70 * 1000);
        counts = runtimeService.createProcessInstanceQuery().count();
        System.out.println("sleep后流程实例数:" + counts);
        return "success";
    }

    /**
     * 使用消息来启动流程，应用场景有mq消息队列
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/messageStartEvent", method = RequestMethod.GET)
    @ResponseBody
    public String messageStartEvent() throws Exception
    {
        // 部署后系统自动按时间规则来起动流程实例
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/startEvent/messageStartEvent.bpmn");
        Deployment deployment = deploymentBuilder.deploy();
        // 通过message的name来触发流程的启动
        ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("msgName");
        System.out.println("processInstance id:" + processInstance.getId());
        return "success";
    }

    /**
     * 使用错误码来起动一子流程，错误开始事件只能出现在子流程容器里面。
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/errorStartEvent", method = RequestMethod.GET)
    @ResponseBody
    public String errorStartEvent() throws Exception
    {
        // 部署后系统自动按时间规则来起动流程实例
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/startEvent/errorStartEvent.bpmn");
        Deployment deployment = deploymentBuilder.deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        //System.out.println("processInstance id:" + processInstance.getId());
        return "success";
    }

}
