package com.workflow.flowable.runtime.service;

import java.util.List;
import java.util.Map;

import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public interface WorkflowService {
    ProcessInstance startProcessInstanceByKey(String processDefineKey);

    List<Task> queryTask(String processDefineKey, DelegationState state);

    void submitTaskFormData(String taskId, Map<String, String> properties);
}
