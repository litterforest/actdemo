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

public abstract class TestBaseController {

    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected RuntimeService runtimeService;

    protected Deployment deployBpmn(String bpmnPath)
    {
        // 部署后系统自动按时间规则来起动流程实例
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource(bpmnPath);
        Deployment deployment = deploymentBuilder.deploy();
        //ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        //ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        return deployment;
    }

    protected ProcessInstance deployBpmnAndStartProcessInstance(String bpmnPath)
    {
        Deployment deployment = deployBpmn(bpmnPath);
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        return processInstance;
    }

    protected String getTaskName(Task task)
    {
        if (task == null)
        {
            return "流程结束";
        }
        else
        {
            return "当前流程节点名称：" + task.getName();
        }
    }

}
