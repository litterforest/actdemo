package com.cobee.jobdelegate.servicetask;

import org.activiti.engine.runtime.Execution;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/3.
 */
public class MyBean implements Serializable {
    private static final long serialVersionUID = 5424633268030543687L;

    public String getName()
    {
        return "cobee";
    }

    public void print(Execution exe)
    {
        System.out.println("当前执行流程的ID是:" + exe.getId());
    }

}
