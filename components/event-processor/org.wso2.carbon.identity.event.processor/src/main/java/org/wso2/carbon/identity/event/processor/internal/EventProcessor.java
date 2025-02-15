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

import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.identity.event.processor.EventProcessorService;

import java.util.logging.Logger;

/**
 * Handles event processing for WSO2 Identity Framework.
 */
@Component(
        service = EventProcessorService.class,
        immediate = true
)
public final class EventProcessor implements EventProcessorService {
    /**
     * Activates the event processor component.
     *
     */
    private static final Logger LOGGER =
            Logger.getLogger(EventProcessor.class.getName());

    /**
     * Processes an incoming event.
     *
     * @param event The event message to be processed.
     */
    @Override
    public void processEvent(final String event) {
        LOGGER.info("Processing Event: " + event);
        // Add event processing logic here.
    }
}
