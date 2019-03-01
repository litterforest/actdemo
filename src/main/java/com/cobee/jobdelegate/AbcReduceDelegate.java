package com.cobee.jobdelegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class AbcReduceDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        execution.getVariable("被补尝任务的参数");
        System.out.println("农业银行补尝扣款");
    }

}
