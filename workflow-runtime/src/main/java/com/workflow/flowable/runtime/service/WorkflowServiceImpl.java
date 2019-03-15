package com.workflow.flowable.runtime.service;

import java.util.List;
import java.util.Map;

import org.flowable.engine.FormService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;

    @Override
    public ProcessInstance startProcessInstanceByKey(String processDefineKey) {
        return runtimeService.startProcessInstanceByKey(processDefineKey);
    }

    @Override
    public List<Task> queryTask(String processDefineKey, DelegationState state) {
        return taskService.createTaskQuery()
                .processDefinitionKey(processDefineKey)
                .taskDelegationState(state)
                .list();
    }

    @Override
    public void submitTaskFormData(String taskId, Map<String, String> properties) {
        formService.submitTaskFormData(taskId, properties);
    }

}
