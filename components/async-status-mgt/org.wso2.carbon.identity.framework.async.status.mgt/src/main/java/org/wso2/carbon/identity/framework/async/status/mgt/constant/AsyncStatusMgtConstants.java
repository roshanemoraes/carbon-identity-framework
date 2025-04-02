package org.wso2.carbon.identity.framework.async.status.mgt.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusTableColumns.IDN_CREATED_AT;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusTableColumns.IDN_OPERATION_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusTableColumns.IDN_OPERATION_STATUS_MESSAGE;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusTableColumns.IDN_RESIDENT_RESOURCE_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusTableColumns.IDN_TARGET_ORG_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusTableColumns.IDN_UNIT_OPERATION_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusTableColumns.IDN_UNIT_OPERATION_STATUS;

/**
 * Asynchronous operation status management constants
 */
public class AsyncStatusMgtConstants {

    public static final String ERROR_PREFIX = "ASM-";
    public static final String DESC_SORT_ORDER = "DESC";
    public static final String ASC_SORT_ORDER = "ASC";
    private static final Map<String, String> attributeColumnMap = new HashMap<>();
    public static final Map<String, String> ATTRIBURE_COLUMN_MAP = Collections.unmodifiableMap(attributeColumnMap);

    public static final String EQ = "eq";
    public static final String CO = "co";
    public static final String SW = "sw";
    public static final String EW = "ew";
    public static final String GE = "ge";
    public static final String LE = "le";
    public static final String GT = "gt";
    public static final String LT = "lt";
    public static final String AND = "and";

    public static final String PAGINATION_AFTER = "after";
    public static final String PAGINATION_BEFORE = "before";

    static {
        attributeColumnMap.put("unitOperationId", IDN_UNIT_OPERATION_ID);
        attributeColumnMap.put("operationId", IDN_OPERATION_ID);
        attributeColumnMap.put("operationInitiatedResourceId", IDN_RESIDENT_RESOURCE_ID);
        attributeColumnMap.put("targetOrgId", IDN_TARGET_ORG_ID);
        attributeColumnMap.put("unitOperationStatus", IDN_UNIT_OPERATION_STATUS);
        attributeColumnMap.put("statusMessage", IDN_OPERATION_STATUS_MESSAGE);
        attributeColumnMap.put("createdTime", IDN_CREATED_AT);
        attributeColumnMap.put(PAGINATION_AFTER, IDN_CREATED_AT);
        attributeColumnMap.put(PAGINATION_BEFORE, IDN_CREATED_AT);
    }

    /**
     * Enum for Error Message
     */
    public static enum ErrorMessages {
        ERROR_CODE_INVALID_REQUEST_BODY("xx001", "Invalid request.", "Provided request body content is not in the expected format.");

        private final String code;
        private final String message;
        private final String description;

        private ErrorMessages(String code, String message, String description) {

            this.code = code;
            this.message = message;
            this.description = description;
        }

        public String getCode() {

            return ERROR_PREFIX + code;
        }

        public String getMessage() {

            return message;
        }

        public String getDescription() {

            return description;
        }

        @Override
        public String toString() {

            return code + " | " + message;
        }
    }



}
