package com.cobee.jobdelegate.subprocess.transaction;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class LockInSeatDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("锁定座位处理...");
    }
}
