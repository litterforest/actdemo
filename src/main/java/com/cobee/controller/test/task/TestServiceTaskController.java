package com.cobee.controller.test.task;

import com.cobee.controller.test.TestBaseController;
import com.cobee.jobdelegate.servicetask.MyBean;
import com.cobee.jobdelegate.servicetask.MyDelegateExpressDelegate;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/3.
 */
@Controller
@RequestMapping("/test/ServiceTask")
public class TestServiceTaskController extends TestBaseController {

    /**
     *
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/delegateExpServiceTask", method = RequestMethod.GET)
    @ResponseBody
    public String delegateExpServiceTask() throws Exception
    {
        Deployment deployment = super.deployBpmn("myprocess/servicetask/delegateExpresstionServiceTask.bpmn");
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        MyDelegateExpressDelegate myDelegateExpressDelegate = new MyDelegateExpressDelegate();
        Map<String, Object> map = new HashMap<>();
        map.put("myDelegateExpressDelegate", myDelegateExpressDelegate);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), map);
        // 查看当前任务
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));
        return "success";
    }

    /**
     * 测试通过javabean的方式来关联对象
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/expressionServiceTask", method = RequestMethod.GET)
    @ResponseBody
    public String expressionServiceTask() throws Exception
    {
        Deployment deployment = super.deployBpmn("myprocess/servicetask/expressionServiceTask.bpmn");
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        Map<String, Object> map = new HashMap<>();
        map.put("myBean", new MyBean());
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), map);
        // 查看流程实例的全局变量
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();
        System.out.println(runtimeService.getVariable(execution.getId(), "myName"));



        return "success";
    }

}
