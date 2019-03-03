package com.cobee.listener;

import org.activiti.engine.delegate.DelegateTask;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/3.
 */
public class MyCreateBeanListener implements Serializable {

    private static final long serialVersionUID = 2634833986356676871L;

    public void handle(DelegateTask task) {
        System.out.println("bean方式注入监听器，当前taskId" + task.getId());
    }

}
