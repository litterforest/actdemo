package com.cobee.controller.test;

import com.cobee.pojo.Person;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
    @Autowired
    private RuntimeService runtimeService;

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
     * 接收任务，claim声明任务的代理人
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
        // 相当于设置了数据库表"act_ru_task"中ASSIGNEE_字段的值
        taskService.claim(task.getId(), user.getId());
        // 不能被重复claim
        //taskService.claim(task.getId(), "1");

        // 完成任务，任务完成后就会流转到下一下节点
        taskService.complete(task.getId());

        return "success";
    }

    /**
     * 设置任务关联的变量数据，基本数据
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/baseTaskVari", method = RequestMethod.GET)
    @ResponseBody
    public String baseTaskVari() throws Exception
    {
        String taskId = UUID.randomUUID().toString().replaceAll("-", "");
        Task task = taskService.newTask(taskId);
        task.setName("测试任务-baseTaskVari");
        taskService.saveTask(task);

        taskService.setVariable(task.getId(), "name", "cobee");

        return "success";
    }

    /**
     * 设置任务关联的变量数据，复杂类型数据
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/classTaskVari", method = RequestMethod.GET)
    @ResponseBody
    public Person classTaskVari() throws Exception
    {
        String taskId = UUID.randomUUID().toString().replaceAll("-", "");
        Task task = taskService.newTask(taskId);
        task.setName("测试任务-baseTaskVari");
        taskService.saveTask(task);

        Person person = new Person();
        person.setAge(18);
        person.setName("cobee");
        taskService.setVariable(task.getId(), "person", person);

        // 通过API查询任务变量person数量
        Person dbPerson = taskService.getVariable(task.getId(), "person", Person.class);

        return dbPerson;
    }

    /**
     * 全局变量与局部变量的区别
     * variableLocal: 当前节点处理完，变量数据就获取不了
     * variable: 当前节点处理完了，流程还没结整，变量数据都能读取出来
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/variAndLocalVari", method = RequestMethod.GET)
    @ResponseBody
    public String variAndLocalVari() throws Exception
    {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.setVariable(task.getId(), "key1", "Hello world");
        System.out.println("当前节点：" + task.getId() + "," + task.getName() + "。variable key1:" + taskService.getVariable(task.getId(), "key1"));

        // 设置完成当前节点
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点：" + task.getId() + "," + task.getName() + "。variable key1:" + taskService.getVariable(task.getId(), "key1"));

        return "success";
    }

    /**
     * 数据对象设置dataObject
     * 直接在bpmn文件中定义变量数据，然后在程序代码中读取出来
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/testDataObject", method = RequestMethod.GET)
    @ResponseBody
    public String testDataObject() throws Exception
    {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前节点：" + task.getId() + "," + task.getName() + "。variable personName:" + taskService.getVariable(task.getId(), "personName"));

        return "success";
    }

    /**
     * 为流程任务设置附件
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/taskAttachment", method = RequestMethod.GET)
    @ResponseBody
    public String taskAttachment() throws Exception
    {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        // 创建任务图片附件
        Resource resource = new ClassPathResource("pig.jpg");
        Attachment attachment = taskService.createAttachment("web url", task.getId(), task.getProcessInstanceId(), "小猪佩奇", "小猪佩奇图片", resource.getInputStream());
        taskService.saveAttachment(attachment);

        // 根据taskId来查找关联的附近
        List<Attachment> attachments = taskService.getTaskAttachments(task.getId());
        System.out.println("attachments size:" + attachments.size());

        return "success";
    }

}
