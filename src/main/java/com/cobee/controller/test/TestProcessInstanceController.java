package com.cobee.controller.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/test/ProcessInstance")
public class TestProcessInstanceController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    /**
     * 单流程实例执行情况，执行数据存放在"act_ru_execution"表里
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/singleProcessExecution", method = RequestMethod.GET)
    @ResponseBody
    public String singleProcessExecution() throws Exception
    {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/single.bpmn");
        Deployment deployment = deploymentBuilder.deploy();

        // 查找流程定义对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(deployment.getId());
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());

        return "success ProcessInstance id:" + processInstance.getId();
    }

    /**
     * 多分支流程实例执行情况，使用平行网关实现。执行数据存放在“act_ru_execution”表中
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/multiProcessExecution", method = RequestMethod.GET)
    @ResponseBody
    public String multiProcessExecution() throws Exception
    {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/multi.bpmn");
        Deployment deployment = deploymentBuilder.deploy();

        // 查找流程定义对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(deployment.getId());
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());

        return "success ProcessInstance id:" + processInstance.getId();
    }

    /**
     * 测试分支执行流程变量数据范围
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/scopeExecution", method = RequestMethod.GET)
    @ResponseBody
    public String scopeExecution() throws Exception
    {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("myprocess/scope.bpmn");
        Deployment deployment = deploymentBuilder.deploy();

        // 查找流程定义对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(deployment.getId());
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();

        for (Task ta : tasks)
        {
            // 给A设置局部变量，
            if ("TaskA".equalsIgnoreCase(ta.getName()))
            {
                runtimeService.setVariableLocal(ta.getExecutionId(), "TaskA", "ValueA");
            }
            // 给B设置全局变量
            else if ("TaskB".equalsIgnoreCase(ta.getName()))
            {
                runtimeService.setVariable(ta.getExecutionId(), "TaskB", "ValueB");
            }
        }

        // 完成这两个任务
        for (Task ta : tasks)
        {
            taskService.complete(ta.getId());
        }

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        System.out.println("TaskA " + runtimeService.getVariableLocal(task.getExecutionId(), "TaskA"));
        System.out.println("TaskB " + runtimeService.getVariable(task.getExecutionId(), "TaskB"));

        return "success ProcessInstance id:" + processInstance.getId();
    }

}
