package org.flowable.cloud.runtime.core.behavior.classdelegate;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.MapExceptionEntry;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.DynamicBpmnConstants;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.helper.ClassDelegate;
import org.flowable.engine.impl.bpmn.helper.ErrorPropagation;
import org.flowable.engine.impl.bpmn.parser.FieldDeclaration;
import org.flowable.engine.impl.context.BpmnOverrideContext;
import org.flowable.engine.impl.util.CommandContextUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public class CloudClassDelegateServiceTaskBehavior extends ClassDelegate {

    private CloudClassDelegateServiceTaskStages taskStages;


    public CloudClassDelegateServiceTaskBehavior(CloudClassDelegateServiceTaskStages taskStage, String id, String className, List<FieldDeclaration> fieldDeclarations, boolean triggerable, Expression skipExpression, List<MapExceptionEntry> mapExceptions) {
        super(id, className, fieldDeclarations, triggerable, skipExpression, mapExceptions);
        this.taskStages = taskStage;
    }

    public CloudClassDelegateServiceTaskBehavior(CloudClassDelegateServiceTaskStages taskStage, String className, List<FieldDeclaration> fieldDeclarations) {
        super(className, fieldDeclarations);
        this.taskStages = taskStage;
    }

    @Override
    public void execute(DelegateExecution execution) {
        if (CommandContextUtil.getProcessEngineConfiguration().isEnableProcessDefinitionInfoCache()) {
            ObjectNode taskElementProperties = BpmnOverrideContext.getBpmnOverrideElementProperties(serviceTaskId, execution.getProcessDefinitionId());
            if (taskElementProperties != null && taskElementProperties.has(DynamicBpmnConstants.SERVICE_TASK_CLASS_NAME)) {
                String overrideClassName = taskElementProperties.get(DynamicBpmnConstants.SERVICE_TASK_CLASS_NAME).asText();
                if (StringUtils.isNotEmpty(overrideClassName) && !overrideClassName.equals(className)) {
                    className = overrideClassName;
                }
            }
        }
        try {
            taskStages.sendCloudRequest(execution, className);
        }
        catch (BpmnError error) {
            ErrorPropagation.propagateError(error, execution);
        }
    }

    @Override
    public void trigger(DelegateExecution execution, String signalName, Object signalData) {
        leave(execution);
    }
}
