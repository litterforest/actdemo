package com.cobee.controller.test;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 测试中间事件
 */
@Controller
@RequestMapping("/test/IntermediateEvent")
public class TestIntermediateEventController extends TestBaseController {

    /**
     * 场景：模拟淘宝的下单流程，先创建订单 -> 超时末支付撤消定单
     * 定时器中间事件
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/timerIntermediateEvent", method = RequestMethod.GET)
    @ResponseBody
    public String timerIntermediateEvent() throws Exception
    {
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/intermediateEvent/timerIntermediateEvent.bpmn");

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        // 买家完成创建订单
        taskService.complete(task.getId());

        //System.out.println(">>>>>>>>>>>>>>>>>>>>>等待超时");
        //Thread.sleep(10 * 1000);
        // 买家完成支付
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));
        taskService.complete(task.getId());

        // 正常结束流程
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        return "success";
    }

    /**
     * 场景：模拟淘宝的下单流程，先创建订单 -> 超时末支付撤消定单
     * 信号中间事件
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/signalIntermediateEvent", method = RequestMethod.GET)
    @ResponseBody
    public String signalIntermediateEvent() throws Exception
    {
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/intermediateEvent/signalIntermediateEvent.bpmn");

        // 完成用户支付
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));
        taskService.complete(task.getId());

        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        System.out.println("当前流程任务：");
        for (Task ta : taskList)
        {
            System.out.println("    " + ta.getName());
            taskService.complete(ta.getId());
        }

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        return "success";
    }

    /**
     * 消息中间事件
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/messageIntermediateEvent", method = RequestMethod.GET)
    @ResponseBody
    public String messageIntermediateEvent() throws Exception
    {
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/intermediateEvent/messageIntermediateEvent.bpmn");

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));
        // 完成TaskA
        taskService.complete(task.getId());

//        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
//        System.out.println(getTaskName(task));

        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();

        // 向流程发送消息
        runtimeService.messageEventReceived("myMessage", execution.getId());

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));
        // 完成TaskB
        taskService.complete(task.getId());

        return "success";
    }

    /**
     * 中间补尝事件，补尝事件可以进行多次。
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/compensationIntermediateEvent", method = RequestMethod.GET)
    @ResponseBody
    public String compensationIntermediateEvent() throws Exception
    {
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/intermediateEvent/compensationIntermediateEvent.bpmn");

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));
        // 模拟工商银行扣款
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));
        // 模拟农来银行加款
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));
        return "success";
    }

}
