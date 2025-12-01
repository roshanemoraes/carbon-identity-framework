/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.wso2.carbon.identity.application.mgt;

import org.testng.annotations.Test;
import org.wso2.carbon.identity.application.common.model.Property;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests for {@link AbstractInboundAuthenticatorConfig} helper behaviour.
 */
public class AbstractInboundAuthenticatorConfigTest {

    @Test
    public void testRelyingPartyKeyMatched() {

        Property property = new Property();
        property.setName("sample-key");

        AbstractInboundAuthenticatorConfig config = new TestConfig("sample-key", property);

        assertTrue(config.isRelyingPartyKeyConfigured(),
                "Expected relying party key to be detected when property names match");
    }

    @Test
    public void testRelyingPartyKeyMissing() {

        Property property = new Property();
        property.setName("sample-key");

        AbstractInboundAuthenticatorConfig config = new TestConfig("different-key", property);

        assertFalse(config.isRelyingPartyKeyConfigured(),
                "Relying party key should not resolve when property names differ");
    }

    private static class TestConfig extends AbstractInboundAuthenticatorConfig {

        private final String relyingPartyKey;
        private final Property[] properties;

        private TestConfig(String relyingPartyKey, Property... properties) {

            this.relyingPartyKey = relyingPartyKey;
            this.properties = properties;
        }

        @Override
        public String getName() {

            return "test";
        }

        @Override
        public String getConfigName() {

            return "config";
        }

        @Override
        public String getFriendlyName() {

            return "friendly";
        }

        @Override
        public Property[] getConfigurationProperties() {

            return properties;
        }

        @Override
        public String getRelyingPartyKey() {

            return relyingPartyKey;
        }
    }
}

