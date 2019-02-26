package com.cobee.controller.test;

import org.activiti.engine.ManagementService;
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
    @Autowired
    private ManagementService managementService;

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

    /**
     * 定时器事件，当在预定时间到达后，自动完成该节点
     * 使用TimercatchingEvent元素来实现定时执行工作，当定时任务完成了才会流转到下一个节点
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/timerJob", method = RequestMethod.GET)
    @ResponseBody
    public String timerJob() throws Exception
    {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/timercatchingevent.bpmn");
        Deployment deployment = deploymentBuilder.deploy();

        // 查找流程定义对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(deployment.getId());
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        System.out.println("processInstance id:" + processInstance.getId());
        return "success";
    }

    /**
     * 测试流程挂起与重启
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/suspendAndActivateJob", method = RequestMethod.GET)
    @ResponseBody
    public String suspendAndActivateJob() throws Exception
    {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/suspend.bpmn");
        Deployment deployment = deploymentBuilder.deploy();

        // 查找流程定义对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(deployment.getId());
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        System.out.println("processInstance id:" + processInstance.getId());

        // 10秒后把流程挂起
        Thread.sleep(10000);
        runtimeService.suspendProcessInstanceById(processInstance.getId());

        // 20秒后再把流程启动起来
        Thread.sleep(20000);
        runtimeService.activateProcessInstanceById(processInstance.getId());
        return "success";
    }

    /**
     * 工作重试3次都失败后，会进入死信工作表“act_ru_deadletter_job”
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/deadletterJob", method = RequestMethod.GET)
    @ResponseBody
    public String deadletterJob() throws Exception
    {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/deadletter.bpmn");
        Deployment deployment = deploymentBuilder.deploy();

        // 查找流程定义对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(deployment.getId());
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        System.out.println("processInstance id:" + processInstance.getId());
        //Thread.sleep(300000);
        return "success";
    }

}
