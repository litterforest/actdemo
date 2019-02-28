package com.cobee.controller.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
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

/**
 * 边界事件不能单独存在，一般和子流程或者补尝事件来结合使用
 */
@Controller
@RequestMapping("/test/boundaryEvent")
public class TestBoundaryEventController extends TestBaseController {

    /**
     * 定时器边界事件，与用户任务一起使用，如果当前的用户任务在特定时间内还没有处理完成。就会自动流转到下一个用户任务
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/timerBoundaryEvent", method = RequestMethod.GET)
    @ResponseBody
    public String timerBoundaryEvent() throws Exception
    {
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/boundaryEvent/timerBoundaryEvent.bpmn");
        // 查看当前节点
        Task task = super.taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点名称:" + task.getName());
        // 等待70秒
        Thread.sleep(70 * 1000);
        // 查看超时后的节点
        task = super.taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点名称:" + task.getName());
        return "success";
    }



}
