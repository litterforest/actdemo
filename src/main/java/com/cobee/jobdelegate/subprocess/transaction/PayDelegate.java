package com.cobee.jobdelegate.subprocess.transaction;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class PayDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        boolean flag = true;
        if (flag)
        {
            System.out.println("支付处理成功...");
        }
        else
        {
            System.out.println("支付处理失败...");
            throw new BpmnError("payError");
        }
    }
}
