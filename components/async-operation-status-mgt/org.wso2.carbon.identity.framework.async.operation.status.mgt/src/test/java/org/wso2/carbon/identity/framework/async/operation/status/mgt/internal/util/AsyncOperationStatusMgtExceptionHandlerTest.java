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

package org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.util;

import org.testng.annotations.Test;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.constants.ErrorMessage;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.exception.AsyncOperationStatusMgtClientException;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.exception.AsyncOperationStatusMgtRuntimeException;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.exception.AsyncOperationStatusMgtServerException;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for {@link AsyncOperationStatusMgtExceptionHandler}.
 */
public class AsyncOperationStatusMgtExceptionHandlerTest {

    @Test
    public void testHandleClientExceptionWithoutData() {

        AsyncOperationStatusMgtClientException exception =
                AsyncOperationStatusMgtExceptionHandler.handleClientException(
                        ErrorMessage.ERROR_CODE_INVALID_LIMIT);

        assertEquals(exception.getErrorCode(), ErrorMessage.ERROR_CODE_INVALID_LIMIT.getCode());
        assertEquals(exception.getMessage(), ErrorMessage.ERROR_CODE_INVALID_LIMIT.getMessage());
        assertEquals(exception.getDescription(), ErrorMessage.ERROR_CODE_INVALID_LIMIT.getDescription());
    }

    @Test
    public void testHandleClientExceptionWithData() {

        AsyncOperationStatusMgtClientException exception =
                AsyncOperationStatusMgtExceptionHandler.handleClientException(
                        ErrorMessage.ERROR_CODE_INVALID_OPERATION_ID, "op-123");

        assertEquals(exception.getErrorCode(), ErrorMessage.ERROR_CODE_INVALID_OPERATION_ID.getCode());
        assertEquals(exception.getMessage(), ErrorMessage.ERROR_CODE_INVALID_OPERATION_ID.getMessage());
        assertEquals(exception.getDescription(),
                String.format(ErrorMessage.ERROR_CODE_INVALID_OPERATION_ID.getDescription(), "op-123"));
    }

    @Test
    public void testHandleServerException() {

        Throwable cause = new RuntimeException("boom");
        AsyncOperationStatusMgtServerException exception =
                AsyncOperationStatusMgtExceptionHandler.handleServerException(
                        ErrorMessage.ERROR_WHILE_PERSISTING_ASYNC_OPERATION_STATUS, cause, "op-123");

        assertEquals(exception.getErrorCode(), ErrorMessage.ERROR_WHILE_PERSISTING_ASYNC_OPERATION_STATUS.getCode());
        assertEquals(exception.getMessage(), ErrorMessage.ERROR_WHILE_PERSISTING_ASYNC_OPERATION_STATUS.getMessage());
        assertEquals(exception.getDescription(),
                String.format(ErrorMessage.ERROR_WHILE_PERSISTING_ASYNC_OPERATION_STATUS.getDescription(), "op-123"));
        assertEquals(exception.getCause(), cause);
    }

    @Test(expectedExceptions = AsyncOperationStatusMgtRuntimeException.class)
    public void testThrowRuntimeException() throws AsyncOperationStatusMgtRuntimeException {

        AsyncOperationStatusMgtExceptionHandler.throwRuntimeException("Runtime error", new RuntimeException("boom"));
    }
}

