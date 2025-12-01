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

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.core.util.IdentityUtil;

import static org.mockito.Mockito.mockStatic;
import static org.testng.Assert.assertFalse;

/**
 * Additional branch coverage for {@link ApplicationMgtUtil#validateRoles()}.
 */
public class ApplicationMgtUtilAdditionalTest {

    private MockedStatic<IdentityUtil> identityUtilMock;

    @AfterMethod
    public void tearDown() {

        if (identityUtilMock != null) {
            identityUtilMock.close();
        }
    }

    @Test
    public void testValidateRolesConfigurationDisabled() {

        identityUtilMock = mockStatic(IdentityUtil.class, Mockito.CALLS_REAL_METHODS);
        identityUtilMock.when(() -> IdentityUtil.getProperty(
                        ApplicationConstants.ENABLE_APPLICATION_ROLE_VALIDATION_PROPERTY))
                .thenReturn("false");

        assertFalse(ApplicationMgtUtil.validateRoles(),
                "Explicitly disabling role validation should return false");
    }

    @Test
    public void testValidateRolesMalformedValueDefaultsToTrue() {

        identityUtilMock = mockStatic(IdentityUtil.class, Mockito.CALLS_REAL_METHODS);
        identityUtilMock.when(() -> IdentityUtil.getProperty(
                        ApplicationConstants.ENABLE_APPLICATION_ROLE_VALIDATION_PROPERTY))
                .thenReturn("invalid-value");

        assertFalse(ApplicationMgtUtil.validateRoles(),
                "Non-boolean configuration should be treated as false");
    }
}
