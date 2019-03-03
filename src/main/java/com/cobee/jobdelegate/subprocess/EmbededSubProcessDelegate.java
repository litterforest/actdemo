package com.cobee.jobdelegate.subprocess;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * Created by Administrator on 2019/3/3.
 */
public class EmbededSubProcessDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("执行嵌入式子流程处理....");
        //throw new BpmnError("myError");
    }

}
