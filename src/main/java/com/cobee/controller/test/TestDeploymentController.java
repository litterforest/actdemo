package com.cobee.controller.test;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * Created by Administrator on 2019/2/24.
 */
@Controller
@RequestMapping(value = "/test/deployment")
public class TestDeploymentController {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 使用zip数据包
     * @return
     */
    @RequestMapping(value = "/doDeployment", method = RequestMethod.GET)
    @ResponseBody
    public String doDeployment() throws Exception
    {
        // 1,加载zip文件数据流
        Resource resource = new ClassPathResource("myprocess/bpmnfiles.zip");
        ZipInputStream zis = new ZipInputStream(resource.getInputStream());

        // 2,执行流程文件部署
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addZipInputStream(zis);
        deploymentBuilder.deploy();
        return "success";
    }

    /**
     * 使用java代码的方式来生部署流程
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/doBpmnDeploy", method = RequestMethod.GET)
    @ResponseBody
    public String doBpmnDeploy() throws Exception
    {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addBpmnModel("test_bpmnmodel", createBpmnModel());
        deploymentBuilder.deploy();
        return "success";
    }

    private BpmnModel createBpmnModel()
    {
        BpmnModel bpmnModel = new BpmnModel();
        // 创建流程定义
        Process process = new Process();
        process.setId("codedefineprocess");
        process.setName("代码动态定义流程");
        // 创建开始事件
        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        process.addFlowElement(startEvent);
        // 创建用户任务
        UserTask userTask = new UserTask();
        userTask.setId("userTask");
        userTask.setName("userTask");
        process.addFlowElement(userTask);
        // 创建结束流程
        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent");
        process.addFlowElement(endEvent);
        // 创建流程节点关系，连线关系
        process.addFlowElement(new SequenceFlow("startEvent", "userTask"));
        process.addFlowElement(new SequenceFlow("userTask", "endEvent"));
        bpmnModel.addProcess(process);
        return bpmnModel;
    }

    /**
     * 手工部署一个流程文件，并用代码把流程文件的内容读出来
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/deployAndRead", method = RequestMethod.GET)
    @ResponseBody
    public String deployAndRead() throws Exception
    {
        String resourceName = "myprocess/my_text.txt";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource(resourceName);
        Deployment deployment = deploymentBuilder.deploy();

        InputStream is = repositoryService.getResourceAsStream(deployment.getId(), resourceName);
        int byteCount = is.available();
        byte[] bytes = new byte[byteCount];
        is.read(bytes);
        is.close();
        return "success, content is : " + new String(bytes);
    }

    /**
     * 查询bpmn流程文档的定义内容
     * 查询流程定义
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryBpmnProcessDefinition", method = RequestMethod.GET)
    @ResponseBody
    public String queryBpmnProcessDefinition(String depId) throws Exception
    {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(depId);
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        InputStream is = repositoryService.getProcessModel(processDefinition.getId());

        int byteCount = is.available();
        byte[] bytes = new byte[byteCount];
        is.read(bytes);
        is.close();
        String content = new String(bytes);
        return "success, content is : " + content;
    }

    /**
     * 读取流程图片
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryProcessDiagram", method = RequestMethod.GET)
    @ResponseBody
    public void queryProcessDiagram(String depId, HttpServletResponse response) throws Exception
    {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(depId);
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        InputStream is = repositoryService.getProcessDiagram(processDefinition.getId());
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        IOUtils.copy(is, servletOutputStream);
        response.flushBuffer();
    }

}
