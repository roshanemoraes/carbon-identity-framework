package org.wso2.carbon.identity.event.processor;

/**
 * Activates the event processor component.
 *
 */
public interface EventProcessorService {
    /**
     * Process an event.
     *
     * @param event The event to be processed.
     */
    void processEvent(String event);
}
