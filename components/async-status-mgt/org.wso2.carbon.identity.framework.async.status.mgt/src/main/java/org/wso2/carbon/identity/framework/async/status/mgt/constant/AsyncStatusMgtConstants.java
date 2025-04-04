package org.wso2.carbon.identity.framework.async.status.mgt.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusModelProperties.MODEL_CORRELATION_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusModelProperties.MODEL_LAST_MODIFIED;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusModelProperties.MODEL_OPERATION_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusModelProperties.MODEL_OPERATION_INITIATED_ORG_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusModelProperties.MODEL_OPERATION_INITIATED_USER_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusModelProperties.MODEL_OPERATION_POLICY;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusModelProperties.MODEL_OPERATION_STATUS;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusModelProperties.MODEL_OPERATION_SUBJECT_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusModelProperties.MODEL_OPERATION_SUBJECT_TYPE;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusModelProperties.MODEL_OPERATION_TYPE;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusTableColumns.IDN_CORRELATION_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusTableColumns.IDN_LAST_MODIFIED;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusTableColumns.IDN_OPERATION_INITIATED_ORG_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusTableColumns.IDN_OPERATION_INITIATED_USER_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusTableColumns.IDN_OPERATION_POLICY;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusTableColumns.IDN_OPERATION_STATUS;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusTableColumns.IDN_OPERATION_SUBJECT_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusTableColumns.IDN_OPERATION_SUBJECT_TYPE;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.OperationStatusTableColumns.IDN_OPERATION_TYPE;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusModelProperties.MODEL_CREATED_AT;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusModelProperties.MODEL_OPERATION_STATUS_MESSAGE;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusModelProperties.MODEL_RESIDENT_RESOURCE_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusModelProperties.MODEL_TARGET_ORG_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusModelProperties.MODEL_UNIT_OPERATION_ID;
import static org.wso2.carbon.identity.framework.async.status.mgt.constant.SQLConstants.UnitOperationStatusModelProperties.MODEL_UNIT_OPERATION_STATUS;
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
    public static final String FILTER_PLACEHOLDER_PREFIX = "FILTER_ID_";

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
        attributeColumnMap.put(MODEL_UNIT_OPERATION_ID, IDN_UNIT_OPERATION_ID);
        attributeColumnMap.put(MODEL_OPERATION_ID, IDN_OPERATION_ID);
        attributeColumnMap.put(MODEL_RESIDENT_RESOURCE_ID, IDN_RESIDENT_RESOURCE_ID);
        attributeColumnMap.put(MODEL_TARGET_ORG_ID, IDN_TARGET_ORG_ID);
        attributeColumnMap.put(MODEL_UNIT_OPERATION_STATUS, IDN_UNIT_OPERATION_STATUS);
        attributeColumnMap.put(MODEL_OPERATION_STATUS_MESSAGE, IDN_OPERATION_STATUS_MESSAGE);
        attributeColumnMap.put(MODEL_CREATED_AT, IDN_CREATED_AT);

        attributeColumnMap.put(MODEL_CORRELATION_ID, IDN_CORRELATION_ID);
        attributeColumnMap.put(MODEL_OPERATION_TYPE, IDN_OPERATION_TYPE);
        attributeColumnMap.put(MODEL_OPERATION_SUBJECT_TYPE, IDN_OPERATION_SUBJECT_TYPE);
        attributeColumnMap.put(MODEL_OPERATION_SUBJECT_ID, IDN_OPERATION_SUBJECT_ID);
        attributeColumnMap.put(MODEL_OPERATION_INITIATED_ORG_ID, IDN_OPERATION_INITIATED_ORG_ID);
        attributeColumnMap.put(MODEL_OPERATION_INITIATED_USER_ID, IDN_OPERATION_INITIATED_USER_ID);
        attributeColumnMap.put(MODEL_OPERATION_STATUS, IDN_OPERATION_STATUS);
        attributeColumnMap.put(MODEL_LAST_MODIFIED, IDN_LAST_MODIFIED);
        attributeColumnMap.put(MODEL_OPERATION_POLICY, IDN_OPERATION_POLICY);

        attributeColumnMap.put(PAGINATION_AFTER, IDN_CREATED_AT);
        attributeColumnMap.put(PAGINATION_BEFORE, IDN_CREATED_AT);
    }

    /**
     * Enum for Error Message
     */
    public static enum ErrorMessages {
        ERROR_CODE_INVALID_REQUEST_BODY("xx001", "Invalid request.",
                "Provided request body content is not in the expected format.");

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
