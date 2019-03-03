package com.cobee.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/3.
 */
public class MyAssignmentListener implements TaskListener, Serializable {

    private static final long serialVersionUID = -7286555000766917039L;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("执行任务代理人分配");
    }

}
