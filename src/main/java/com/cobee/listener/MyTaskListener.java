package com.cobee.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;

/**
 * Created by Administrator on 2019/3/3.
 */
public class MyTaskListener implements TaskListener {

    private FixedValue userName;

    public void setUserName(FixedValue userName) {
        this.userName = userName;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("任务监听器执行处理......userName:" + userName.getExpressionText());
    }

}
