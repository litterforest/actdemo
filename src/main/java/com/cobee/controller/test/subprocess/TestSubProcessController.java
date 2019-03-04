package com.cobee.controller.test.subprocess;

import com.cobee.controller.test.TestBaseController;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 描述activiti里面的子流程，其中事件子流程就是由事件触发的子流程
 *
 * Created by Administrator on 2019/3/3.
 */
@Controller
@RequestMapping("/test/subProcess")
public class TestSubProcessController extends TestBaseController {

    /**
     * 嵌入式子流程
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/embededSubProcess", method = RequestMethod.GET)
    @ResponseBody
    public String embededSubProcess() throws Exception
    {
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/subprocess/EmbededSubprocess.bpmn");
        // 查看当前节点
        Task task = super.taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));
        return "success";
    }

    /**
     * 调用子流程
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/callSubProcess", method = RequestMethod.GET)
    @ResponseBody
    public String callSubProcess() throws Exception
    {
        // pre：先部署子流程
        //Deployment deployment = super.deployBpmn("myprocess/subprocess/BeCallAudit.bpmn");

        // 走正式测试
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/subprocess/CallActivitiSubProcess.bpmn");
        System.out.println("processInstance id:" + processInstance.getId());
        // 查看当前节点
        Task task = super.taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));
        // *完成员工填写表单
        taskService.complete(task.getId());

        // 查看当前节点
        task = super.taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        // 通过父流程实例查询子流程实例
        ProcessInstance subProcessInstance = runtimeService.createProcessInstanceQuery().superProcessInstanceId(processInstance.getId()).singleResult();

        // 查看调用子流程的任务
        task = taskService.createTaskQuery().processInstanceId(subProcessInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        // 总监审核
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(subProcessInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        // 总经理审核
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(subProcessInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        // 查看主流程状态
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        return "success";
    }

    /**
     * 事务子流程，事务里面的任务处理会得到回滚，通过补尝处理实现
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/transactionSubProcess", method = RequestMethod.GET)
    @ResponseBody
    public String transactionSubProcess() throws Exception
    {
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/subprocess/transactionSubprocess.bpmn");

        System.out.println("ProcessInstance id:" + processInstance.getId());

        // 查看场次选定任务
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        // 完成场次选下任务
        taskService.complete(task.getId());

        // 查看当前用户任务
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        return "success";
    }

}
