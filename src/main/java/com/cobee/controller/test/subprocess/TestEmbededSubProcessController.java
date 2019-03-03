package com.cobee.controller.test.subprocess;

import com.cobee.controller.test.TestBaseController;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 嵌入式子流程
 *
 * Created by Administrator on 2019/3/3.
 */
@Controller
@RequestMapping("/test/EmbededSubProcess")
public class TestEmbededSubProcessController extends TestBaseController {

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

}
