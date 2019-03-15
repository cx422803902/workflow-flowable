package org.flowable.cloud.runtime.core.behavior.classdelegate;

import java.util.List;

import org.flowable.bpmn.model.MapExceptionEntry;
import org.flowable.cloud.runtime.core.utils.NameUtil;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.impl.bpmn.helper.ClassDelegate;
import org.flowable.engine.impl.bpmn.helper.DefaultClassDelegateFactory;
import org.flowable.engine.impl.bpmn.parser.FieldDeclaration;
import org.springframework.context.ApplicationContext;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public class CloudClassDelegateFactory extends DefaultClassDelegateFactory {
    private ApplicationContext applicationContext;
    private CloudClassDelegateServiceTaskStages taskStage;

    public CloudClassDelegateFactory(ApplicationContext applicationContext, CloudClassDelegateServiceTaskStages taskStage) {
        this.applicationContext = applicationContext;
        this.taskStage = taskStage;
    }

    @Override
    public ClassDelegate create(String id, String className, List<FieldDeclaration> fieldDeclarations, boolean triggerable, Expression skipExpression, List<MapExceptionEntry> mapExceptions) {
        if (this.applicationContext.containsBean(NameUtil.getJavaDelegateBeanName(className))) {
            return new CloudClassDelegateServiceTaskBehavior(taskStage, id, className, fieldDeclarations, triggerable, skipExpression, mapExceptions);
        }
        else {
            return super.create(id, className, fieldDeclarations, triggerable, skipExpression, mapExceptions);
        }
    }

    @Override
    public ClassDelegate create(String className, List<FieldDeclaration> fieldDeclarations) {
        if (this.applicationContext.containsBean(NameUtil.getJavaDelegateBeanName(className))) {
            return new CloudClassDelegateServiceTaskBehavior(taskStage, className, fieldDeclarations);
        }
        else {
            return super.create(className, fieldDeclarations);
        }
    }
}
