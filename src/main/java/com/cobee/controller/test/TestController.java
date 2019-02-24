package com.cobee.controller.test;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2019/2/23.
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello()
    {
        return "hello";
    }

    @RequestMapping(value = "/start/{processKey}", method = RequestMethod.GET)
    @ResponseBody
    public String start(@PathVariable String processKey)
    {

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();


        return "success start process, 流程的当前节点是" + task;
    }

}
