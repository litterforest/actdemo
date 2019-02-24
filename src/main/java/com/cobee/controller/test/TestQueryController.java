package com.cobee.controller.test;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Administrator on 2019/2/23.
 */
@Controller
@RequestMapping("/test/query")
public class TestQueryController {

    @Autowired
    private IdentityService identityService;

    /**
     * 创建10个组数据
     * @return
     */
    @RequestMapping(value = "/initGroupData", method = RequestMethod.GET)
    @ResponseBody
    public String initGroupData()
    {
        for (int i = 1; i <= 10; i++)
        {
            Group group = identityService.newGroup(String.valueOf(i));
            group.setName("Group" + i);
            group.setType("Type" + i);
            identityService.saveGroup(group);
        }
        return "success";
    }

    /**
     * 查询所有用户组数据
     * @return
     */
    @RequestMapping(value = "/listGroup", method = RequestMethod.GET)
    @ResponseBody
    public List<Group> listGroup()
    {
        GroupQuery groupQuery = identityService.createGroupQuery();
        //groupQuery.list();
        return groupQuery.list();
    }

    /**
     * 分页查询用户组数据
     * @return
     */
    @RequestMapping(value = "/listPageGroup", method = RequestMethod.GET)
    @ResponseBody
    public List<Group> listGroup(Integer startIdx, Integer nums)
    {
        GroupQuery groupQuery = identityService.createGroupQuery();
        return groupQuery.listPage(startIdx, nums);
    }

    /**
     * 查询数据的数量
     * @return
     */
    @RequestMapping(value = "/listGroupCount", method = RequestMethod.GET)
    @ResponseBody
    public Long listGroupCount()
    {
        GroupQuery groupQuery = identityService.createGroupQuery();
        return groupQuery.count();
    }

}
