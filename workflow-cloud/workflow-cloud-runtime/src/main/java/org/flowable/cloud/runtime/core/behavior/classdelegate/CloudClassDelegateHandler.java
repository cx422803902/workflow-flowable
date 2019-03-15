package org.flowable.cloud.runtime.core.behavior.classdelegate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.flowable.app.engine.AppEngine;
import org.flowable.cloud.runtime.core.model.CloudContextEntity;
import org.flowable.common.engine.impl.context.Context;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.util.ReflectionUtils;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public class CloudClassDelegateHandler extends HandlerMethod {
    private CloudClassDelegateServiceTaskStages connectorStages;
    private AppEngine appEngine;

    public CloudClassDelegateHandler(Object bean, Method method) {
        super(bean, method);
    }

    @Autowired
    public void setConnectorStages(CloudClassDelegateServiceTaskStages connectorStages) {
        this.connectorStages = connectorStages;
    }

    @Autowired
    public void setAppEngine(AppEngine appEngine) {
        this.appEngine = appEngine;
    }

    public void handlerMessage(CloudContextEntity cloudContextEntity) throws Exception {
        appEngine.getAppEngineConfiguration().getCommandExecutor().execute(commandContext -> {

            DelegateExecution delegateExecution = CommandContextUtil
                    .getExecutionEntityManager(Context.getCommandContext())
                    .findById(cloudContextEntity.getExecutionId());
            Object[] args = new Object[]{delegateExecution};
            ReflectionUtils.makeAccessible(getBridgedMethod());
            try {
                getBridgedMethod().invoke(getBean(), args);
                connectorStages.sendCloudResult(delegateExecution, cloudContextEntity.getClassName());
            }
            catch (IllegalArgumentException ex) {
                String text = (ex.getMessage() != null ? ex.getMessage() : "Illegal argument");
                throw new RuntimeException(formatInvokeError(text, args), ex);
            }catch (IllegalAccessException ea) {
                String text = (ea.getMessage() != null ? ea.getMessage() : "Illegal access");
                throw new RuntimeException(formatInvokeError(text, args), ea);
            }
            catch (InvocationTargetException ex) {
                // Unwrap for HandlerExceptionResolvers ...
                Throwable targetException = ex.getTargetException();
                if (targetException instanceof RuntimeException) {
                    throw (RuntimeException) targetException;
                }
                else if (targetException instanceof Error) {
                    throw (Error) targetException;
                }
                else if (targetException instanceof Exception) {
                    throw new RuntimeException(targetException);
                }
                else {
                    throw new IllegalStateException(formatInvokeError("Invocation failure", args), targetException);
                }
            }
            return null;
        });

    }

    private String formatInvokeError(String text, Object[] args) {

        String formattedArgs = IntStream.range(0, args.length)
                .mapToObj(i -> (
                        args[i] != null ?
                                "[" + i + "] [type=" + args[i].getClass().getName() + "] [value=" + args[i] + "]" :
                                "[" + i + "] [null]"))
                .collect(Collectors.joining(",\n", " ", " "));

        return text + "\n" +
                "Controller [" + getBeanType().getName() + "]\n" +
                "Method [" + getBridgedMethod().toGenericString() + "] " +
                "with argument values:\n" + formattedArgs;
    }
}
