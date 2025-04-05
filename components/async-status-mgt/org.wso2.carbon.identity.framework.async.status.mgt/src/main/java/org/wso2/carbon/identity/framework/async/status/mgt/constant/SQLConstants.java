package org.wso2.carbon.identity.framework.async.status.mgt.constant;

/**
 * This class contains database queries related to CRUD operations for status of asynchronous operations.
 */
public class SQLConstants {

    public static final String LIMIT = "LIMIT";

    public static final String CREATE_ASYNC_OPERATION_IDN = "INSERT INTO IDN_ASYNC_OPERATION_STATUS(" +
            "IDN_CORRELATION_ID, IDN_OPERATION_TYPE, IDN_OPERATION_SUBJECT_TYPE, IDN_OPERATION_SUBJECT_ID," +
            "IDN_OPERATION_INITIATED_ORG_ID, IDN_OPERATION_INITIATED_USER_ID, IDN_OPERATION_STATUS," +
            "IDN_CREATED_AT, IDN_LAST_MODIFIED, IDN_OPERATION_POLICY) VALUES(" +
            ":" + OperationStatusTableColumns.IDN_CORRELATION_ID + ";, " +
            ":" + OperationStatusTableColumns.IDN_OPERATION_TYPE + ";, " +
            ":" + OperationStatusTableColumns.IDN_OPERATION_SUBJECT_TYPE + ";, " +
            ":" + OperationStatusTableColumns.IDN_OPERATION_SUBJECT_ID + ";, " +
            ":" + OperationStatusTableColumns.IDN_OPERATION_INITIATED_ORG_ID + ";, " +
            ":" + OperationStatusTableColumns.IDN_OPERATION_INITIATED_USER_ID + ";, " +
            ":" + OperationStatusTableColumns.IDN_OPERATION_STATUS + ";, " +
            ":" + OperationStatusTableColumns.IDN_CREATED_AT + ";, " +
            ":" + OperationStatusTableColumns.IDN_LAST_MODIFIED + ";, " +
            ":" + OperationStatusTableColumns.IDN_OPERATION_POLICY + ";)";

    public static final String CREATE_ASYNC_OPERATION_UNIT_IDN = "INSERT INTO IDN_ASYNC_OPERATION_STATUS_UNIT (" +
            "IDN_OPERATION_ID, IDN_RESIDENT_RESOURCE_ID, IDN_TARGET_ORG_ID," +
            "IDN_UNIT_OPERATION_STATUS, IDN_OPERATION_STATUS_MESSAGE, IDN_CREATED_AT ) VALUES(" +
            ":" + UnitOperationStatusTableColumns.IDN_OPERATION_ID + ";, " +
            ":" + UnitOperationStatusTableColumns.IDN_RESIDENT_RESOURCE_ID + ";, " +
            ":" + UnitOperationStatusTableColumns.IDN_TARGET_ORG_ID + ";, " +
            ":" + UnitOperationStatusTableColumns.IDN_UNIT_OPERATION_STATUS + ";, " +
            ":" + UnitOperationStatusTableColumns.IDN_OPERATION_STATUS_MESSAGE + ";, " +
            ":" + UnitOperationStatusTableColumns.IDN_CREATED_AT + ";)";

    public static final String FETCH_LATEST_ASYNC_OPERATION_IDN =
            "SELECT IDN_OPERATION_ID, IDN_OPERATION_TYPE, IDN_OPERATION_SUBJECT_TYPE, IDN_OPERATION_SUBJECT_ID, " +
                    "IDN_OPERATION_INITIATED_ORG_ID, IDN_OPERATION_INITIATED_USER_ID, IDN_OPERATION_STATUS, IDN_OPERATION_POLICY " +
                    "FROM IDN_ASYNC_OPERATION_STATUS " +
                    "WHERE IDN_OPERATION_SUBJECT_ID = :" + OperationStatusTableColumns.IDN_OPERATION_SUBJECT_ID + " " +
                    "AND IDN_OPERATION_INITIATED_ORG_ID = :" + OperationStatusTableColumns.IDN_OPERATION_INITIATED_ORG_ID + " " +
                    "AND IDN_OPERATION_POLICY = :" + OperationStatusTableColumns.IDN_OPERATION_POLICY + " " +
                    "AND IDN_OPERATION_INITIATED_USER_ID = :" + OperationStatusTableColumns.IDN_OPERATION_INITIATED_USER_ID +
                    " " +
                    "ORDER BY IDN_CREATED_AT DESC " +
                    "LIMIT 1;";

    public static final String GET_OPERATIONS = "SELECT IDN_OPERATION_ID, IDN_CORRELATION_ID, IDN_OPERATION_TYPE, " +
            "IDN_OPERATION_SUBJECT_TYPE, IDN_OPERATION_SUBJECT_ID, IDN_OPERATION_INITIATED_ORG_ID, " +
            "IDN_OPERATION_INITIATED_USER_ID, IDN_OPERATION_STATUS, IDN_OPERATION_POLICY, IDN_CREATED_AT, " +
            "IDN_LAST_MODIFIED FROM IDN_ASYNC_OPERATION_STATUS " +
            "WHERE IDN_OPERATION_SUBJECT_TYPE = :OPERATION_SUBJECT_TYPE; " +
            "AND IDN_OPERATION_SUBJECT_ID = :OPERATION_SUBJECT_ID; AND IDN_OPERATION_TYPE = :OPERATION_TYPE; ";

    public static final String GET_OPERATIONS_TAIL = " ORDER BY IDN_CREATED_AT DESC LIMIT :LIMIT; ;";

    public static final String GET_UNIT_OPERATIONS = "SELECT IDN_UNIT_OPERATION_ID, IDN_OPERATION_ID, " +
            "IDN_RESIDENT_RESOURCE_ID, IDN_TARGET_ORG_ID, IDN_UNIT_OPERATION_STATUS, IDN_OPERATION_STATUS_MESSAGE, " +
            "IDN_CREATED_AT FROM IDN_ASYNC_OPERATION_STATUS_UNIT WHERE IDN_OPERATION_ID = :OPERATION_ID; ";

    public static final String GET_UNIT_OPERATIONS_TAIL = " ORDER BY IDN_CREATED_AT DESC LIMIT :LIMIT; ;";

    /**
     * SQL Placeholders.
     */
    //todo: RENAME columns->placeholders
    public static class OperationStatusTableColumns {

        public static final String IDN_OPERATION_ID = "IDN_OPERATION_ID";
        public static final String IDN_CORRELATION_ID = "IDN_CORRELATION_ID";
        public static final String IDN_OPERATION_TYPE = "IDN_OPERATION_TYPE";
        public static final String IDN_OPERATION_SUBJECT_TYPE = "IDN_OPERATION_SUBJECT_TYPE";
        public static final String IDN_OPERATION_SUBJECT_ID = "IDN_OPERATION_SUBJECT_ID";
        public static final String IDN_OPERATION_INITIATED_ORG_ID = "IDN_OPERATION_INITIATED_ORG_ID";
        public static final String IDN_OPERATION_INITIATED_USER_ID = "IDN_OPERATION_INITIATED_USER_ID";
        public static final String IDN_OPERATION_STATUS = "IDN_OPERATION_STATUS";
        public static final String IDN_CREATED_AT = "IDN_CREATED_AT";
        public static final String IDN_LAST_MODIFIED = "IDN_LAST_MODIFIED";
        public static final String IDN_OPERATION_POLICY = "IDN_OPERATION_POLICY";
    }

    /**
     * SQL Placeholders.
     */
    public static class UnitOperationStatusTableColumns {

        public static final String IDN_UNIT_OPERATION_ID = "IDN_UNIT_OPERATION_ID";
        public static final String IDN_OPERATION_ID = "IDN_OPERATION_ID";
        public static final String IDN_RESIDENT_RESOURCE_ID = "IDN_RESIDENT_RESOURCE_ID";
        public static final String IDN_TARGET_ORG_ID = "IDN_TARGET_ORG_ID";
        public static final String IDN_UNIT_OPERATION_STATUS = "IDN_UNIT_OPERATION_STATUS";
        public static final String IDN_OPERATION_STATUS_MESSAGE = "IDN_OPERATION_STATUS_MESSAGE";
        public static final String IDN_CREATED_AT = "IDN_CREATED_AT";
    }

    public static class UnitOperationStatusModelProperties {

        public static final String MODEL_UNIT_OPERATION_ID = "UNIT_OPERATION_ID";
        public static final String MODEL_RESIDENT_RESOURCE_ID = "OPERATION_INITIATED_RESOURCE_ID";
        public static final String MODEL_TARGET_ORG_ID = "TARGET_ORG_ID";
        public static final String MODEL_UNIT_OPERATION_STATUS = "UNIT_OPERATION_STATUS";
        public static final String MODEL_OPERATION_STATUS_MESSAGE = "STATUS_MESSAGE";
        public static final String MODEL_CREATED_AT = "CREATED_TIME";
    }

    public static class OperationStatusModelProperties {

        public static final String MODEL_OPERATION_ID = "OPERATION_ID";
        public static final String MODEL_CORRELATION_ID = "CORRELATION_ID";
        public static final String MODEL_OPERATION_TYPE = "OPERATION_TYPE";
        public static final String MODEL_OPERATION_SUBJECT_TYPE = "OPERATION_SUBJECT_TYPE";
        public static final String MODEL_OPERATION_SUBJECT_ID = "OPERATION_SUBJECT_ID";
        public static final String MODEL_OPERATION_INITIATED_ORG_ID = "RESIDENT_ORG_ID";
        public static final String MODEL_OPERATION_INITIATED_USER_ID = "INITIATOR_ID";
        public static final String MODEL_OPERATION_STATUS = "OPERATION_STATUS";
        public static final String MODEL_CREATED_TIME = "CREATED_TIME";
        public static final String MODEL_LAST_MODIFIED = "MODIFIED_TIME";
        public static final String MODEL_OPERATION_POLICY = "OPERATION_POLICY";
    }

}
