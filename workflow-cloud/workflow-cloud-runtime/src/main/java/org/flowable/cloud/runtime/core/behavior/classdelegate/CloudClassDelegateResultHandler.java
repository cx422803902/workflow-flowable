package org.flowable.cloud.runtime.core.behavior.classdelegate;

import org.flowable.cloud.runtime.core.model.CloudContextEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public class CloudClassDelegateResultHandler {
    public static final String INTEGRATION_RESULTS_PRODUCER = "integrationResult";
    public static final String INTEGRATION_RESULTS_CONSUMER = "integrationResultsConsumer";
    @Autowired
    private CloudClassDelegateServiceTaskStages cloudClassDelegateServiceTaskStages;

    @StreamListener(INTEGRATION_RESULTS_CONSUMER)
    public void receiveCloudResult(CloudContextEntity cloudContextEntity) {
        cloudClassDelegateServiceTaskStages.receiveCloudResult(cloudContextEntity);
    }

    public interface ProcessResultChannels {

        @Input(INTEGRATION_RESULTS_CONSUMER)
        SubscribableChannel integrationResultsConsumer();
    }
}
