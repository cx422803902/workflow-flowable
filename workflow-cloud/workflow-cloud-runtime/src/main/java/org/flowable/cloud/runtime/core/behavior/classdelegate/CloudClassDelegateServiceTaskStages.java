package org.flowable.cloud.runtime.core.behavior.classdelegate;

import org.flowable.cloud.runtime.core.model.CloudContextEntity;
import org.flowable.engine.delegate.DelegateExecution;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public interface CloudClassDelegateServiceTaskStages {
    void sendCloudRequest(DelegateExecution execution, String className);

    void sendCloudResult(DelegateExecution execution, String className);

    void receiveCloudResult(CloudContextEntity contextEntity);
}
