package com.cobee.controller.test.task;

import com.cobee.component.AuthService;
import com.cobee.controller.test.TestBaseController;
import com.cobee.listener.MyCreateBeanListener;
import com.cobee.listener.MyCreateDelegateListener;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  介绍用户任务和与之相关联的任务监听器
 *
 *
 * Created by Administrator on 2019/3/2.
 */
@Controller
@RequestMapping("/test/userTask")
public class TestUserTaskController extends TestBaseController {

    @Autowired
    private AuthService authService;

    /**
     * 设置用户任务认领人
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/candidateUserTask", method = RequestMethod.GET)
    @ResponseBody
    public String candidateUserTask() throws Exception
    {
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/usertask/candidateUserTask.bpmn");

        // 查看boss用户组下面有多个少可认领的任务
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("boss").list();
        for (Task ta : tasks)
        {
            System.out.print(ta.getName() + ",");
        }
        System.out.println();

        // 查看groupA组下面有多少个可以认领的任务
        tasks = taskService.createTaskQuery().taskCandidateGroup("Group6").list();
        for (Task ta : tasks)
        {
            System.out.print(ta.getName() + ",");
        }
        System.out.println();

        // 查看cobee用户下面有多少个可以认领的任务
        tasks = taskService.createTaskQuery().taskCandidateUser("cobee").list();
        for (Task ta : tasks)
        {
            System.out.print(ta.getName() + ",");
        }
        System.out.println();

        // 测试使用匿名用户来完成任务
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(task.getId());
        System.out.println(super.getTaskName(task));
        return "success";
    }

    /**
     * 通过juel表达式来设置用户任务的认领候选人
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/juelCandidateUserTask", method = RequestMethod.GET)
    @ResponseBody
    public String juelCandidateUserTask() throws Exception
    {
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("myprocess/usertask/juelCandidateUserTask.bpmn").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("authService", authService);
        runtimeService.startProcessInstanceById(processDefinition.getId(), maps);

        return "success";
    }

    /**
     * 为用户任务增加一个创建阶段的监听器
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/userTaskCreateListener", method = RequestMethod.GET)
    @ResponseBody
    public String userTaskCreateListener() throws Exception
    {
        super.deployBpmnAndStartProcessInstance("myprocess/usertask/userTaskListenerCreate.bpmn");

        return "success";
    }

    /**
     * 为用户任务增加一个创建阶段的监听器，使用expression方式配置监听器
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/userTaskCreateListenerExpress", method = RequestMethod.GET)
    @ResponseBody
    public String userTaskCreateListenerExpress() throws Exception
    {
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("myprocess/usertask/userTaskListenerCreate.bpmn").deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("myCreateBeanListener", new MyCreateBeanListener());
        runtimeService.startProcessInstanceById(processDefinition.getId(), maps);

        return "success";
    }

    /**
     * 为用户任务增加一个创建阶段的监听器，使用delegateExpression方式配置监听器
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/userTaskCreateListenerDelegExpress", method = RequestMethod.GET)
    @ResponseBody
    public String userTaskCreateListenerDelegExpress() throws Exception
    {
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("myprocess/usertask/userTaskListenerCreate.bpmn").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("myCreateBeanListener", new MyCreateBeanListener());
        maps.put("myCreateDelegateListener", new MyCreateDelegateListener());
        runtimeService.startProcessInstanceById(processDefinition.getId(), maps);
        return "success";
    }

    /**
     * 多种类型任务监听器，包含create, assignment, complete
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/mutipluTaskListener", method = RequestMethod.GET)
    @ResponseBody
    public String mutipluTaskListener() throws Exception
    {
        ProcessInstance processInstance = super.deployBpmnAndStartProcessInstance("myprocess/usertask/userTaskListenerCreate.bpmn");

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        // 分配任务代理人
        taskService.claim(task.getId(), "cobee");

        // 完成任务
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println(getTaskName(task));

        return "success";
    }

}
