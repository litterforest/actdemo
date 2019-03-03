package com.cobee.jobdelegate.servicetask;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/3.
 */
public class MyDelegateExpressDelegate implements JavaDelegate, Serializable {


    private static final long serialVersionUID = -2822117688328896073L;

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("通过juel表达式来设置service task的处理类");
    }
}
