/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.event.processor.internal;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.wso2.carbon.identity.event.processor.EventProcessorService;

import java.util.logging.Logger;

/**
 * OSGi component for managing the event processing service.
 */
@Component(
        name = "org.wso2.carbon.identity.framework.internal."
                + "EventProcessorServiceComponent",
        immediate = true
)
public class EventProcessorServiceComponent {

    /**
     * Activates the event processor component.
     *
     */
    private static final Logger LOGGER =
            Logger.getLogger(EventProcessorServiceComponent.class.getName());
    /**
     * Activates the event processor component.
     *
     */
    private EventProcessorService eventProcessorService;

    /**
     * Activates the event processor component.
     *
     * @param context The OSGi component context.
     */
    @Activate
    protected void activate(final ComponentContext context) {
        LOGGER.info("Event Processor Service Component is activated");
        eventProcessorService.processEvent("B2B App Share");
    }

    /**
     * Deactivates the event processor component.
     *
     * @param context The OSGi component context.
     */
    @Deactivate
    protected void deactivate(final ComponentContext context) {
        LOGGER.info("Event Processor Service Component is deactivated");
    }

    /**
     * Binds the Event Processor Service.
     *
     * @param service The EventProcessorService implementation.
     */
    @Reference
    protected void setEventProcessorService(
            final EventProcessorService service) {
        this.eventProcessorService = service;
    }

    /**
     * Unbinds the Event Processor Service.
     *
     * @param service The EventProcessorService implementation.
     */
    protected void unsetEventProcessorService(
            final EventProcessorService service) {
        this.eventProcessorService = null;
    }
}
