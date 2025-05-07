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

package org.wso2.carbon.identity.framework.async.operation.status.mgt.buffer;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.buffer.SubOperationStatusObject;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.buffer.SubOperationStatusQueue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.constants.TestAsyncOperationConstants.STATUS_SUCCESS;

public class SubOperationStatusQueueTest {

    @DataProvider(name = "operationStatusProvider")
    public Object[][] operationStatusProvider() {

        SubOperationStatusObject obj1 = new SubOperationStatusObject(STATUS_SUCCESS);
        SubOperationStatusObject obj2 = new SubOperationStatusObject("FAILED");
        SubOperationStatusObject obj3 = new SubOperationStatusObject("PARTIALLY_COMPLETED");

        return new Object[][]{
                {Collections.emptyList(), STATUS_SUCCESS},
                {Collections.singletonList(obj1), STATUS_SUCCESS},
                {Collections.singletonList(obj2), "FAILED"},
                {Collections.singletonList(obj3), "PARTIALLY_COMPLETED"},
                {Arrays.asList(obj1, obj2), "PARTIALLY_COMPLETED"},
                {Arrays.asList(obj1, obj3), "PARTIALLY_COMPLETED"},
                {Arrays.asList(obj2, obj3), "PARTIALLY_COMPLETED"},
        };
    }

    @Test(dataProvider = "operationStatusProvider")
    public void testRegisterOperationStatusWithoutUpdate(List<SubOperationStatusObject> statusList,
                                                         String expectedStatus) {

        SubOperationStatusQueue subOperationList = new SubOperationStatusQueue();
        for (SubOperationStatusObject obj : statusList) {
            subOperationList.add(obj);
        }
        assertEquals(expectedStatus, subOperationList.getOperationStatus());
    }
}
