package com.workflow.flowable.server.business;

import org.flowable.cloud.runtime.core.behavior.classdelegate.annotation.CloudClassDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import com.alibaba.fastjson.JSON;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
@CloudClassDelegate
public class TestServiceTast1  implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("this is test1: " + JSON.toJSONString(execution.getVariables()));
    }
}