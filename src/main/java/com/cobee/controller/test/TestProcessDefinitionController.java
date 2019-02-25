package com.cobee.controller.test;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * 测试流程定义控制器
 *
 *
 * Created by Administrator on 2019/2/24.
 */
@Controller
@RequestMapping(value = "/test/processdefinition")
public class TestProcessDefinitionController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IdentityService identityService;

    /**
     * 根据部署来中止流程定义
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/suspend", method = RequestMethod.GET)
    @ResponseBody
    public String suspend(String depId) throws Exception
    {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(depId);
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        repositoryService.suspendProcessDefinitionById(processDefinition.getId());
        return "success";
    }

    /**
     * 激活流程定义
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    @ResponseBody
    public String activate(String depId) throws Exception
    {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(depId);
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        repositoryService.activateProcessDefinitionById(processDefinition.getId());
        return "success";
    }

    /**
     * 激活流程定义，使用的时候通过查询权限表act_ru_identitylink，来查看该用户下有哪些可用流程
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/authProcess", method = RequestMethod.GET)
    @ResponseBody
    public String authProcess(String depId) throws Exception
    {
        UserQuery userQuery = identityService.createUserQuery();
        User user = userQuery.userId("1").singleResult();
        if (user == null)
        {
            // 创建activiti用户
            user = identityService.newUser("1");
            user.setEmail("552477192@qq.com");
            user.setFirstName("chen");
            user.setLastName("cobee");
            user.setPassword("123456");
            identityService.saveUser(user);
        }

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.deploymentId(depId);
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();
        repositoryService.addCandidateStarterUser(processDefinition.getId(), user.getId());

        return "success";
    }

}
