package com.cobee.controller.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * # 边界事件不能单独存在，一般和子流程或者补尝事件来结合使用
 * # 错误边界事件需要与错误结束事件来一起使用
 * # 边界事件一般是触发另一个任务元素
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

    /**
     * 信号边界事件，当发出信号时可以改变流程的走向，让流程往回走，比如说合同的查看和确认
     * 必须是流程到了那个节点，发signal信号才能触发流转
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/signalBoundaryEvent", method = RequestMethod.GET)
    @ResponseBody
    public String signalBoundaryEvent() throws Exception
    {
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/boundaryEvent/signalBoundaryEvent.bpmn");

        // 查看“合同查看”节点
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点名称：" + task.getName());
        // 完成当前节点的处理
        taskService.complete(task.getId());
        // 查看“合同确认”节点
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点名称：" + task.getName());
        // 发出信息，让节点回退到“合同查看”
        runtimeService.signalEventReceived("contactChangeSignal");
        // 查看当前节点流转到哪里，正常应该是“合同变更”
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点名称：" + task.getName());
        // 完成合同变更处理
        taskService.complete(task.getId());
        // 当前节点应该在“查看合同”
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点名称：" + task.getName());
        // 这时合同审核流程从头开始走
        // 这时的节点是“合同审核”
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点名称：" + task.getName());
        // 完成“合同确认”，结束该流程实例
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点名称：" + (task == null ? "end" : task.getName()));
        return "success";
    }

}
