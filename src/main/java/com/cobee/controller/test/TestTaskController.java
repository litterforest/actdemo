package com.cobee.controller.test;

import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

/**
 * activiti工作流，任务组件测试
 */
@Controller
@RequestMapping("/test/task")
public class TestTaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;

    /**
     * 创建流程任务，把流程任务与用户进行候选关联，根据用户查询对应的任务
     * 候选人是多对多的关系，一个任务可以有多个任务组，一个任务组也可以有多个任务
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/taskCandidate", method = RequestMethod.GET)
    @ResponseBody
    public String taskCandidate(String userId) throws Exception
    {
        // 1, 创建任务
//        String taskId = UUID.randomUUID().toString().replaceAll("-", "");
//        Task task = taskService.newTask(taskId);
//        task.setName("测试任务");
//        taskService.saveTask(task);

        // 查询cobee用户
        UserQuery userQuery = identityService.createUserQuery();
        User user = userQuery.userId(userId).singleResult();

        // 把任务和用户关联起来
//        taskService.addCandidateUser(task.getId(), user.getId());

        // 查询用户下面有哪些任务
        TaskQuery taskQuery = taskService.createTaskQuery();
        taskQuery.taskCandidateUser(user.getId());
        List<Task> taskList = taskQuery.list();

        StringBuilder sbuff = new StringBuilder();
        if (!CollectionUtils.isEmpty(taskList))
        {
            for (Task task : taskList)
            {
                sbuff.append(task.getName()).append(";");
            }
        }


        return sbuff.toString();
    }

    /**
     * 设置任务的持有人 owner
     * 任务与持有人是一对一的关系，就是一个任务只有一个owner，但是一个owner可以有多个任务。
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/taskOwner", method = RequestMethod.GET)
    @ResponseBody
    public String taskOwner(String taskId, String userId) throws Exception
    {
        // 根据ID查询任务数据
        TaskQuery taskQuery = taskService.createTaskQuery();
        taskQuery.taskId(taskId);
        Task task = taskQuery.singleResult();

        // 根据ID查询用户数据
        UserQuery userQuery = identityService.createUserQuery();
        User user = userQuery.userId(userId).singleResult();

        if (task != null && user != null)
        {
            taskService.setOwner(task.getId(), user.getId());
            // 设置任务代理人，当前owner处理不了，可以转给任务代理人
            // taskService.setAssignee(task.getId(), user.getId());
        }

        // 根据用户ID查询，下面的任务数据
        taskQuery = taskService.createTaskQuery();
        List<Task> tasks = taskQuery.taskOwner(user.getId()).list();

        StringBuilder sbuff = new StringBuilder();
        if (!CollectionUtils.isEmpty(tasks))
        {
            for (Task ta : tasks)
            {
                sbuff.append(ta.getName()).append(";");
            }
        }

        return sbuff.toString();
    }

    /**
     * 接收任务
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/claimTask", method = RequestMethod.GET)
    @ResponseBody
    public String claimTask() throws Exception
    {
        String taskId = UUID.randomUUID().toString().replaceAll("-", "");
        Task task = taskService.newTask(taskId);
        task.setName("测试任务-claim");
        taskService.saveTask(task);

        // 创建用户
        UserQuery userQuery = identityService.createUserQuery();
        User user = userQuery.userId("2").singleResult();
        if (user == null)
        {
            // 创建activiti用户
            user = identityService.newUser("2");
            user.setEmail("18027041863@163.com");
            user.setFirstName("chen");
            user.setLastName("gansen");
            user.setPassword("123456");
            identityService.saveUser(user);
        }

        taskService.claim(task.getId(), user.getId());
        taskService.claim(task.getId(), "1");

        return "success";
    }

}
