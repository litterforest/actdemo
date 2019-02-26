package com.cobee.jobdelegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * 总会出错的工作
 */
public class ErrorDelegate implements JavaDelegate {

    private static Integer count = 0;

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("第" + (++count) + "次出错");
        if (1 == 1)
        {
            throw new RuntimeException("job fail");
        }
    }
}
