package com.cobee.jobdelegate;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CountDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("人少了，需要汇报了");
        // 错误事件由错误结束事件来抛出
        //throw new BpmnError("error");
    }

}
