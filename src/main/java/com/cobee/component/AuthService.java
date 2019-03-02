package com.cobee.component;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/3/2.
 */
@Component
public class AuthService implements Serializable {

    /**
     * 返回用户候选人权限，可以通过数据库来查询
     * @return
     */
    public List<String> getCandidateUsers()
    {
        System.out.println("从数据库中查询任务对应的权限");
        return Arrays.asList("cobee", "cobee123");
    }

}
