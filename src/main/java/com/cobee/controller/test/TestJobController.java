package com.cobee.controller.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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

/**
 * 工作类型：
 * 异步工作(默认类型)
 */
@Controller
@RequestMapping("/test/job")
public class TestJobController {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;

    /**
     * 使用serviceTask元素来实现异步执行工作，当异步任务完成了才会流转到下一个节点
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
        System.out.println("processInstance id:" + processInstance.getId());
        //Thread.sleep(1000);

        // 使用执行流的方式来查询
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();
        System.out.println("当前子执行流程:" + execution.getId() + "，" + execution.getActivityId());
        // 等待异步job完成处理
        Thread.sleep(1000);
        execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();
        System.out.println("当前子执行流程:" + execution.getId() + "，" + execution.getActivityId());

        // 查询当前任务节点
//        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
//        System.out.println("当前任务节点:" + task.getId() + "，" + task.getName());

        return "success";
    }

}
