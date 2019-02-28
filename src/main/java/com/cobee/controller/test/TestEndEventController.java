package com.cobee.controller.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 结束事件，一般都是抛出型事件，被开始事件和边界事件捕获
 */
@Controller
@RequestMapping("/test/EndEvent")
public class TestEndEventController {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

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

    /**
     * 测试取消结束事件，取消结束事件只能用在事务子流程中，与取消边界事件一起使用
     * 重点是画出流程图
     * 在事务子流程里面，用户任务处理完之后，流转到取消结束事件。取消结束事件抛出并触发补尝事件。
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cancelEndEvent", method = RequestMethod.GET)
    @ResponseBody
    public String cancelEndEvent() throws Exception
    {
        // 部署后系统自动按时间规则来起动流程实例
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/endEvent/cancelEndEvent.bpmn");
        Deployment deployment = deploymentBuilder.deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());

        System.out.println("processInstance id:" + processInstance.getId());

        // 查找第一个任务
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点名称:" + task.getName());

        // 完成第一个任务，并自动触发一个补尝处理CompensationDelegate
        taskService.complete(task.getId());

        // 查找当前任务节点名称
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点名称:" + task.getName());

        // 完成第二个任务
        //taskService.complete(task.getId());

        // 查找当前任务节点名称
//        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
//        System.out.println("当前节点名称:" + (task == null ? "null" : task.getName()));

        return "success";
    }

    /**
     * 测试终止事件，在整个大流程中，只要有一个节点到达终止事件，整个大执行流都会结束
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/terminalEndEvent", method = RequestMethod.GET)
    @ResponseBody
    public String terminalEndEvent() throws Exception
    {
        // 部署后系统自动按时间规则来起动流程实例
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/endEvent/terminalEndEvent.bpmn");
        Deployment deployment = deploymentBuilder.deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());

        System.out.println("processInstance id:" + processInstance.getId());

        // 审核前查询执行流程的个数
        long exeCounts = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).count();
        System.out.println("当前流程的执行数:" + exeCounts);

        // 模拟审核TaskA
//        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
//        for (Task ta : tasks)
//        {
//            if ("TaskA".equals(ta.getName()))
//            {
//                taskService.complete(ta.getId());
//            }
//        }

        // 模拟审核TaskB
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        for (Task ta : tasks)
        {
            if ("TaskB".equals(ta.getName()) || "TaskC".equals(ta.getName()))
            {
                taskService.complete(ta.getId());
            }
        }

        // 再模拟审核TaskC
        tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        for (Task ta : tasks)
        {
            if ("TaskB".equals(ta.getName()) || "TaskC".equals(ta.getName()))
            {
                taskService.complete(ta.getId());
            }
        }

        // 再查看当前正在执行的流程数
        exeCounts = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).count();
        System.out.println("审核后当前流程的执行数:" + exeCounts);
        return "success";
    }

}
