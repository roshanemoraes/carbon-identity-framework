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

package org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.component;

import org.testng.annotations.Test;
import org.wso2.carbon.identity.organization.management.service.OrganizationManager;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

/**
 * Tests for {@link AsyncOperationStatusMgtDataHolder}.
 */
public class AsyncOperationStatusMgtDataHolderTest {

    @Test
    public void testSetAndGetOrganizationManager() {

        AsyncOperationStatusMgtDataHolder dataHolder = AsyncOperationStatusMgtDataHolder.getInstance();
        dataHolder.setOrganizationManager(null);
        assertNull(dataHolder.getOrganizationManager());

        OrganizationManager organizationManager = mock(OrganizationManager.class);
        dataHolder.setOrganizationManager(organizationManager);
        assertSame(dataHolder.getOrganizationManager(), organizationManager);

        dataHolder.setOrganizationManager(null);
        assertNull(dataHolder.getOrganizationManager());
    }
}

