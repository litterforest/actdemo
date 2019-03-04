package com.cobee.jobdelegate.subprocess.transaction;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CancelLockInSeatDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("取消座位选择处理...");
    }
}
