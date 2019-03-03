package com.cobee.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/3.
 */
public class MyCreateDelegateListener implements TaskListener, Serializable {
    private static final long serialVersionUID = 2091398131037272838L;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("delegateService方式注入监听器，当前taskId:" + delegateTask.getId());
    }
}
