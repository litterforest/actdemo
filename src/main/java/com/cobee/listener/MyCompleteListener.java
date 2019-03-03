package com.cobee.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/3.
 */
public class MyCompleteListener implements TaskListener, Serializable {

    private static final long serialVersionUID = 858266599378932244L;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("任务完成后执行的监听处理行为");
    }
}
