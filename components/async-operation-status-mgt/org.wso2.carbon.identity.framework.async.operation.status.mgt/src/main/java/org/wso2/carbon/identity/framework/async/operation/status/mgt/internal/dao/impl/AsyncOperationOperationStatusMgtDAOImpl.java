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

package org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.database.utils.jdbc.NamedJdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.NamedPreparedStatement;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.database.utils.jdbc.exceptions.TransactionException;
import org.wso2.carbon.identity.core.model.ExpressionNode;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.constants.OperationStatus;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.exception.AsyncOperationStatusMgtException;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.exception.AsyncOperationStatusMgtServerException;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.models.OperationInitDTO;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.models.OperationResponseDTO;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.models.UnitOperationInitDTO;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.api.models.UnitOperationStatusCount;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.dao.AsyncOperationStatusMgtDAO;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.models.FilterQueryBuilder;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.models.dos.UnitOperationDO;
import org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.util.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.wso2.carbon.identity.framework.async.operation.status.mgt.api.constants.ErrorMessage.ERROR_WHILE_PERSISTING_ASYNC_OPERATION_STATUS;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.api.constants.ErrorMessage.ERROR_WHILE_PERSISTING_ASYNC_OPERATION_STATUS_UNIT;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.api.constants.ErrorMessage.ERROR_WHILE_RETRIEVING_ASYNC_OPERATION_STATUS;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.api.constants.ErrorMessage.ERROR_WHILE_RETRIEVING_ASYNC_OPERATION_STATUS_UNIT;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.api.constants.ErrorMessage.ERROR_WHILE_UPDATING_ASYNC_OPERATION_STATUS;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.api.constants.OperationStatus.FAILED;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.api.constants.OperationStatus.PARTIALLY_COMPLETED;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.api.constants.OperationStatus.SUCCESS;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.AsyncOperationStatusMgtConstants.ATTRIBURE_COLUMN_MAP;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.AsyncOperationStatusMgtConstants.EQ;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.AsyncOperationStatusMgtConstants.FILTER_PLACEHOLDER_PREFIX;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.AsyncOperationStatusMgtConstants.GE;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.AsyncOperationStatusMgtConstants.GT;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.AsyncOperationStatusMgtConstants.LE;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.AsyncOperationStatusMgtConstants.LT;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.AsyncOperationStatusMgtConstants.SW;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.CREATE_ASYNC_OPERATION;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.CREATE_ASYNC_OPERATION_UNIT_BATCH;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.DELETE_RECENT_OPERATION_RECORD;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.FilterPlaceholders.CREATED_TIME_FILTER;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_OPERATION;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_OPERATIONS;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_OPERATIONS_TAIL;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_OPERATIONS_TAIL_MSSQL;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_OPERATIONS_TAIL_ORACLE;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_UNIT_OPERATION;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_UNIT_OPERATIONS;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_UNIT_OPERATIONS_TAIL;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_UNIT_OPERATIONS_TAIL_MSSQL;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_UNIT_OPERATIONS_TAIL_ORACLE;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.GET_UNIT_OPERATION_STATUS_COUNT;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.LIMIT;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.CORRELATION_ID;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.CREATED_AT;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.INITIATED_ORG_ID;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.INITIATED_USER_ID;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.LAST_MODIFIED;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.OPERATION_ID;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.OPERATION_TYPE;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.POLICY;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.RESIDENT_RESOURCE_ID;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.STATUS;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.STATUS_MESSAGE;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.SUBJECT_ID;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.SUBJECT_TYPE;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.TARGET_ORG_ID;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.SQLPlaceholders.UNIT_OPERATION_ID;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.constant.SQLConstants.UPDATE_ASYNC_OPERATION;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.util.AsyncOperationStatusMgtExceptionHandler.handleServerException;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.util.Utils.isMSSqlDB;
import static org.wso2.carbon.identity.framework.async.operation.status.mgt.internal.util.Utils.isOracleDB;

/**
 * DAO implementation for Asynchronous Operation Status Management.
 */
public class AsyncOperationOperationStatusMgtDAOImpl implements AsyncOperationStatusMgtDAO {

    private static final Log LOG = LogFactory.getLog(AsyncOperationOperationStatusMgtDAOImpl.class);

    @Override
    public String registerAsyncStatusWithoutUpdate(OperationInitDTO record) throws AsyncOperationStatusMgtException {

        Timestamp currentTimestamp = new Timestamp(new Date().getTime());
        String operationId = UUID.randomUUID().toString();

        try (Connection connection = IdentityDatabaseUtil.getDBConnection(false);
             NamedPreparedStatement statement = new NamedPreparedStatement(connection,
                     CREATE_ASYNC_OPERATION, OPERATION_ID)) {

            statement.setString(OPERATION_ID, operationId);
            statement.setString(CORRELATION_ID, record.getCorrelationId());
            statement.setString(OPERATION_TYPE, record.getOperationType());
            statement.setString(SUBJECT_TYPE, record.getOperationSubjectType());
            statement.setString(SUBJECT_ID, record.getOperationSubjectId());
            statement.setString(INITIATED_ORG_ID, record.getResidentOrgId());
            statement.setString(INITIATED_USER_ID, record.getInitiatorId());
            statement.setString(STATUS, OperationStatus.IN_PROGRESS.toString());
            statement.setTimeStamp(CREATED_AT, currentTimestamp, null);
            statement.setTimeStamp(LAST_MODIFIED, currentTimestamp, null);
            statement.setString(POLICY, record.getOperationPolicy());
            statement.executeUpdate();
            return operationId;
        } catch (SQLException e) {
            throw handleServerException(ERROR_WHILE_PERSISTING_ASYNC_OPERATION_STATUS, e);
        }
    }

    @Override
    public String registerAsyncStatusWithUpdate(OperationInitDTO record) throws AsyncOperationStatusMgtException {

        Timestamp currentTimestamp = new Timestamp(new Date().getTime());
        String operationId = UUID.randomUUID().toString();

        try (Connection connection = IdentityDatabaseUtil.getDBConnection(false)) {
            deleteOldOperationalData(connection, record.getCorrelationId(), record.getOperationType(),
                    record.getOperationSubjectId());

            try (NamedPreparedStatement statement = new NamedPreparedStatement(connection,
                    CREATE_ASYNC_OPERATION, OPERATION_ID)) {
                statement.setString(OPERATION_ID, operationId);
                statement.setString(CORRELATION_ID, record.getCorrelationId());
                statement.setString(OPERATION_TYPE, record.getOperationType());
                statement.setString(SUBJECT_TYPE, record.getOperationSubjectType());
                statement.setString(SUBJECT_ID, record.getOperationSubjectId());
                statement.setString(INITIATED_ORG_ID, record.getResidentOrgId());
                statement.setString(INITIATED_USER_ID, record.getInitiatorId());
                statement.setString(STATUS, OperationStatus.IN_PROGRESS.toString());
                statement.setTimeStamp(CREATED_AT, currentTimestamp, null);
                statement.setTimeStamp(LAST_MODIFIED, currentTimestamp, null);
                statement.setString(POLICY, record.getOperationPolicy());
                statement.executeUpdate();
            }
            return operationId;
        } catch (SQLException e) {
            throw handleServerException(ERROR_WHILE_PERSISTING_ASYNC_OPERATION_STATUS, e);
        }
    }

    @Override
    public void updateAsyncStatus(String operationId, String status) throws AsyncOperationStatusMgtException {

        NamedJdbcTemplate namedJdbcTemplate = Utils.getNewTemplate();
        try {
            namedJdbcTemplate.withTransaction(template -> {
                template.executeUpdate(UPDATE_ASYNC_OPERATION,
                        statement -> {
                            statement.setString(STATUS, status);
                            statement.setTimeStamp(LAST_MODIFIED, new Timestamp(new Date().getTime()), null);
                            statement.setString(OPERATION_ID, operationId);
                        });
                return null;
            });
        } catch (TransactionException e) {
            throw handleServerException(ERROR_WHILE_UPDATING_ASYNC_OPERATION_STATUS, e);
        }
    }

    @Override
    public void registerAsyncStatusUnit(ConcurrentLinkedQueue<UnitOperationInitDTO> queue)
            throws AsyncOperationStatusMgtException {

        Timestamp currentTimestamp = new Timestamp(new Date().getTime());
        NamedJdbcTemplate namedJdbcTemplate = Utils.getNewTemplate();
        try {
            namedJdbcTemplate.withTransaction(template ->
                template.executeBatchInsert(CREATE_ASYNC_OPERATION_UNIT_BATCH,
                    statement -> {
                        for (UnitOperationInitDTO context : queue) {
                            statement.setString(UNIT_OPERATION_ID, UUID.randomUUID().toString());
                            statement.setString(OPERATION_ID, context.getOperationId());
                            statement.setString(RESIDENT_RESOURCE_ID, context.getOperationInitiatedResourceId());
                            statement.setString(TARGET_ORG_ID, context.getTargetOrgId());
                            statement.setString(STATUS, context.getUnitOperationStatus());
                            statement.setString(STATUS_MESSAGE, context.getStatusMessage());
                            statement.setTimeStamp(CREATED_AT, currentTimestamp, null);
                            statement.addBatch();
                        }
                    }, null));
        } catch (TransactionException e) {
            throw handleServerException(ERROR_WHILE_PERSISTING_ASYNC_OPERATION_STATUS_UNIT, e);
        }
    }

    @Override
    public List<OperationResponseDTO> getOperations(String requestInitiatedOrgId, Integer limit,
                                                    List<ExpressionNode> expressionNodes) throws
            AsyncOperationStatusMgtException {

        FilterQueryBuilder filterQueryBuilder = buildFilterQuery(expressionNodes, CREATED_TIME_FILTER);
        String sqlStmt = getOperationsStatusSqlStmt(filterQueryBuilder);

        List<OperationResponseDTO> operationRecords;
        NamedJdbcTemplate namedJdbcTemplate = Utils.getNewTemplate();
        try {
            operationRecords = namedJdbcTemplate.executeQuery(sqlStmt,
                (resultSet, rowNumber) -> {
                    try {
                        return createOperationResponseDTO(resultSet);
                    } catch (DataAccessException e) {
                        throw new RuntimeException(e);
                    }
                },
                namedPreparedStatement -> {
                    setFilterAttributes(namedPreparedStatement, filterQueryBuilder.getFilterAttributeValue(),
                            filterQueryBuilder.getTimestampFilterAttributes());
                    namedPreparedStatement.setInt(LIMIT, limit);
                    namedPreparedStatement.setString(INITIATED_ORG_ID, requestInitiatedOrgId);
                });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_WHILE_RETRIEVING_ASYNC_OPERATION_STATUS, e);
        }
        return operationRecords;
    }

    @Override
    public OperationResponseDTO getOperation(String operationId, String requestInitiatedOrgId) throws
            AsyncOperationStatusMgtException {

        OperationResponseDTO operationRecord;
        NamedJdbcTemplate jdbcTemplate = Utils.getNewTemplate();
        try {
            operationRecord = jdbcTemplate.fetchSingleRecord(GET_OPERATION, (resultSet, rowNumber) -> {
                try {
                    return createOperationResponseDTO(resultSet);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }, namedPreparedStatement -> {
                namedPreparedStatement.setString(OPERATION_ID, operationId);
                namedPreparedStatement.setString(INITIATED_ORG_ID, requestInitiatedOrgId);
            });
            return operationRecord;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_WHILE_RETRIEVING_ASYNC_OPERATION_STATUS, e);
        }
    }

    private OperationResponseDTO createOperationResponseDTO(ResultSet resultSet)
            throws SQLException, DataAccessException {

        return new OperationResponseDTO.Builder()
                .operationId(resultSet.getString(1))
                .correlationId(resultSet.getString(2))
                .operationType(resultSet.getString(3))
                .operationSubjectType(resultSet.getString(4))
                .operationSubjectId(resultSet.getString(5))
                .residentOrgId(resultSet.getString(6))
                .initiatorId(resultSet.getString(7))
                .operationStatus(resultSet.getString(8))
                .operationPolicy(resultSet.getString(9))
                .createdTime(Timestamp.valueOf(resultSet.getString(10)))
                .modifiedTime(Timestamp.valueOf(resultSet.getString(11)))
                .unitStatusCount(getUnitOperationStatusCount(resultSet.getString(1),
                        resultSet.getString(6)))
                .build();
    }

    @Override
    public List<UnitOperationDO> getUnitOperations(String operationId, String requestInitiatedOrgId, Integer limit,
                                                   List<ExpressionNode> expressionNodes)
            throws AsyncOperationStatusMgtServerException {

        FilterQueryBuilder filterQueryBuilder = buildFilterQuery(expressionNodes, CREATED_TIME_FILTER);
        String sqlStmt = getUnitOperationsStatusSqlStmt(filterQueryBuilder);

        List<UnitOperationDO> unitOperationRecords;
        NamedJdbcTemplate namedJdbcTemplate = Utils.getNewTemplate();
        try {
            unitOperationRecords = namedJdbcTemplate.executeQuery(sqlStmt,
                (resultSet, rowNumber) -> {
                    UnitOperationDO record = new UnitOperationDO();
                    record.setUnitOperationId(resultSet.getString(1));
                    record.setOperationId(resultSet.getString(2));
                    record.setOperationInitiatedResourceId(resultSet.getString(3));
                    record.setTargetOrgId(resultSet.getString(4));
                    record.setUnitOperationStatus(resultSet.getString(5));
                    record.setStatusMessage(resultSet.getString(6));
                    record.setCreatedTime(Timestamp.valueOf(resultSet.getString(7)));


                    return record;
                },
                namedPreparedStatement -> {
                    namedPreparedStatement.setString(OPERATION_ID, operationId);
                    setFilterAttributes(namedPreparedStatement, filterQueryBuilder.getFilterAttributeValue(),
                            filterQueryBuilder.getTimestampFilterAttributes());
                    namedPreparedStatement.setInt(LIMIT, limit);
                    namedPreparedStatement.setString(INITIATED_ORG_ID, requestInitiatedOrgId);
                });

        } catch (DataAccessException e) {
            throw handleServerException(ERROR_WHILE_RETRIEVING_ASYNC_OPERATION_STATUS_UNIT, e);
        }
        return unitOperationRecords;
    }

    @Override
    public UnitOperationDO getUnitOperation(String unitOperationId, String requestInitiatedOrgId)
            throws AsyncOperationStatusMgtException {

        UnitOperationDO unitOperationRecord;
        NamedJdbcTemplate namedJdbcTemplate = Utils.getNewTemplate();
        try {
            unitOperationRecord = namedJdbcTemplate.fetchSingleRecord(GET_UNIT_OPERATION, (resultSet, rowNumber) -> {
                if (StringUtils.isBlank(resultSet.getString(1))) {
                    return null;
                }
                UnitOperationDO record = new UnitOperationDO();
                record.setUnitOperationId(resultSet.getString(1));
                record.setOperationId(resultSet.getString(2));
                record.setOperationInitiatedResourceId(resultSet.getString(3));
                record.setTargetOrgId(resultSet.getString(4));
                record.setUnitOperationStatus(resultSet.getString(5));
                record.setStatusMessage(resultSet.getString(6));
                record.setCreatedTime(Timestamp.valueOf(resultSet.getString(7)));
                return record;
            }, namedPreparedStatement -> {
                namedPreparedStatement.setString(UNIT_OPERATION_ID, unitOperationId);
                namedPreparedStatement.setString(INITIATED_ORG_ID, requestInitiatedOrgId);
            });
            return unitOperationRecord;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_WHILE_RETRIEVING_ASYNC_OPERATION_STATUS_UNIT, e);
        }
    }

    private void deleteOldOperationalData(Connection connection, String correlationId, String operationType,
                                          String operationSubjectId) throws SQLException {

        try (NamedPreparedStatement statement = new NamedPreparedStatement(connection,
                DELETE_RECENT_OPERATION_RECORD)) {
            statement.setString(OPERATION_TYPE, operationType);
            statement.setString(SUBJECT_ID, operationSubjectId);
            statement.setString(CORRELATION_ID, correlationId);
            statement.executeUpdate();
        }
    }

    private UnitOperationStatusCount getUnitOperationStatusCount(String operationId, String requestInitiatedOrgId)
            throws  DataAccessException {

        UnitOperationStatusCount countObj = new UnitOperationStatusCount();
        NamedJdbcTemplate namedJdbcTemplate = Utils.getNewTemplate();

        namedJdbcTemplate.executeQuery(GET_UNIT_OPERATION_STATUS_COUNT, (resultSet, rowNumber) -> {

            String status = resultSet.getString(1);
            int count = resultSet.getInt(2);

            if (StringUtils.equals(status, SUCCESS.toString())) {
                countObj.setSuccess(count);
            } else if (StringUtils.equals(status, FAILED.toString())) {
                countObj.setFailed(count);
            } else if (StringUtils.equals(status, PARTIALLY_COMPLETED.toString())) {
                countObj.setPartiallyCompleted(count);
            }
            return null;
        },
        namedPreparedStatement -> {
            namedPreparedStatement.setString(OPERATION_ID, operationId);
            namedPreparedStatement.setString(INITIATED_ORG_ID, requestInitiatedOrgId);
        });
        return countObj;
    }

    private FilterQueryBuilder buildFilterQuery(List<ExpressionNode> expressionNodes, String attributeUsedForCursor)
            throws AsyncOperationStatusMgtServerException {

        FilterQueryBuilder filterQueryBuilder = new FilterQueryBuilder();
        appendFilterQuery(expressionNodes, filterQueryBuilder, attributeUsedForCursor);
        return filterQueryBuilder;
    }

    private void appendFilterQuery(List<ExpressionNode> expressionNodes, FilterQueryBuilder filterQueryBuilder,
                                   String attributeUsedForCursor)
            throws AsyncOperationStatusMgtServerException {

        int count = 1;
        StringBuilder filter = new StringBuilder();
        if (CollectionUtils.isEmpty(expressionNodes)) {
            filterQueryBuilder.setFilterQuery(StringUtils.EMPTY);
        } else {
            for (ExpressionNode expressionNode : expressionNodes) {
                String operation = expressionNode.getOperation();
                String value = expressionNode.getValue();
                String attributeValue = expressionNode.getAttributeValue();
                String attributeName = ATTRIBURE_COLUMN_MAP.get(attributeValue);

                if (StringUtils.isNotBlank(attributeName) && StringUtils.isNotBlank(value) && StringUtils
                        .isNotBlank(operation)) {
                    if (CREATED_AT.equals(attributeName)) {
                        filterQueryBuilder.addTimestampFilterAttributes(FILTER_PLACEHOLDER_PREFIX);
                    }
                    switch (operation) {
                        case EQ: {
                            equalFilterBuilder(count, value, attributeName, filter, filterQueryBuilder);
                            count++;
                            break;
                        }
                        case SW: {
                            startWithFilterBuilder(count, value, attributeName, filter, filterQueryBuilder);
                            count++;
                            break;
                        }
                        case GE: {
                            greaterThanOrEqualFilterBuilder(count, value, attributeName, filter, filterQueryBuilder);
                            count++;
                            break;
                        }
                        case LE: {
                            lessThanOrEqualFilterBuilder(count, value, attributeName, filter, filterQueryBuilder);
                            count++;
                            break;
                        }
                        case GT: {
                            greaterThanFilterBuilder(count, value, attributeName, filter, filterQueryBuilder);
                            count++;
                            break;
                        }
                        case LT: {
                            lessThanFilterBuilder(count, value, attributeName, filter, filterQueryBuilder);
                            count++;
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isBlank(filter.toString())) {
                filterQueryBuilder.setFilterQuery(StringUtils.EMPTY);
            } else {
                if (filter.toString().endsWith("AND ")) {
                    String filterString = filter.toString();
                    filterQueryBuilder.setFilterQuery(filterString.substring(0, filterString.length() - 4));
                } else {
                    filterQueryBuilder.setFilterQuery(filter.toString());
                }
            }
        }
    }

    private void equalFilterBuilder(int count, String value, String attributeName, StringBuilder filter,
                                    FilterQueryBuilder filterQueryBuilder) {

        String filterString = String.format(" = :%s%s; AND ", FILTER_PLACEHOLDER_PREFIX, count);
        filter.append(attributeName).append(filterString);
        filterQueryBuilder.setFilterAttributeValue(FILTER_PLACEHOLDER_PREFIX, value);
    }

    private void startWithFilterBuilder(int count, String value, String attributeName, StringBuilder filter,
                                        FilterQueryBuilder filterQueryBuilder) {

        String filterString = String.format(" like :%s%s; AND ", FILTER_PLACEHOLDER_PREFIX, count);
        filter.append(attributeName).append(filterString);
        filterQueryBuilder.setFilterAttributeValue(FILTER_PLACEHOLDER_PREFIX, value + "%");
    }

    private void greaterThanOrEqualFilterBuilder(int count, String value, String attributeName, StringBuilder filter,
                                                 FilterQueryBuilder filterQueryBuilder)
            throws AsyncOperationStatusMgtServerException {

        String filterString = String.format(isDateTimeAndMSSql(attributeName) ? " >= CAST(:%s%s; AS DATETIME) AND "
                : " >= :%s%s; AND ", FILTER_PLACEHOLDER_PREFIX, count);
        filter.append(attributeName).append(filterString);
        filterQueryBuilder.setFilterAttributeValue(FILTER_PLACEHOLDER_PREFIX, value);
    }

    private void lessThanOrEqualFilterBuilder(int count, String value, String attributeName, StringBuilder filter,
                                              FilterQueryBuilder filterQueryBuilder)
            throws AsyncOperationStatusMgtServerException {

        String filterString = String.format(isDateTimeAndMSSql(attributeName) ? " <= CAST(:%s%s; AS DATETIME) AND "
                : " <= :%s%s; AND ", FILTER_PLACEHOLDER_PREFIX, count);
        filter.append(attributeName).append(filterString);
        filterQueryBuilder.setFilterAttributeValue(FILTER_PLACEHOLDER_PREFIX, value);
    }

    private void greaterThanFilterBuilder(int count, String value, String attributeName, StringBuilder filter,
                                          FilterQueryBuilder filterQueryBuilder)
            throws AsyncOperationStatusMgtServerException {

        String filterString = String.format(isDateTimeAndMSSql(attributeName) ? " > CAST(:%s%s; AS DATETIME) AND "
                : " > :%s%s; AND ", FILTER_PLACEHOLDER_PREFIX, count);
        filter.append(attributeName).append(filterString);
        filterQueryBuilder.setFilterAttributeValue(FILTER_PLACEHOLDER_PREFIX, value);
    }

    private void lessThanFilterBuilder(int count, String value, String attributeName, StringBuilder filter,
                                       FilterQueryBuilder filterQueryBuilder)
            throws AsyncOperationStatusMgtServerException {

        String filterString = String.format(isDateTimeAndMSSql(attributeName) ? " < CAST(:%s%s; AS DATETIME) AND "
                : " < :%s%s; AND ", FILTER_PLACEHOLDER_PREFIX, count);
        filter.append(attributeName).append(filterString);
        filterQueryBuilder.setFilterAttributeValue(FILTER_PLACEHOLDER_PREFIX, value);
    }

    private boolean isDateTimeAndMSSql(String attributeName) throws AsyncOperationStatusMgtServerException {

        return (CREATED_AT.equals(attributeName)) && isMSSqlDB();
    }

    private static String getOperationsStatusSqlStmt(FilterQueryBuilder filterQueryBuilder)
            throws AsyncOperationStatusMgtServerException {

        String sqlStmtTail;
        if (isOracleDB()) {
            sqlStmtTail = GET_OPERATIONS_TAIL_ORACLE;
        } else if (isMSSqlDB()) {
            sqlStmtTail = GET_OPERATIONS_TAIL_MSSQL;
        } else {
            sqlStmtTail = GET_OPERATIONS_TAIL;
        }

        if (StringUtils.isNotBlank(filterQueryBuilder.getFilterQuery())) {
            return GET_OPERATIONS + " AND " + filterQueryBuilder.getFilterQuery() + sqlStmtTail;
        }
        return GET_OPERATIONS + sqlStmtTail;
    }

    private static String getUnitOperationsStatusSqlStmt(FilterQueryBuilder filterQueryBuilder)
            throws AsyncOperationStatusMgtServerException {

        String sqlStmtTail;
        if (isOracleDB()) {
            sqlStmtTail = GET_UNIT_OPERATIONS_TAIL_ORACLE;
        } else if (isMSSqlDB()) {
            sqlStmtTail = GET_UNIT_OPERATIONS_TAIL_MSSQL;
        } else {
            sqlStmtTail = GET_UNIT_OPERATIONS_TAIL;
        }
        if (StringUtils.isNotBlank(filterQueryBuilder.getFilterQuery())) {
            return GET_UNIT_OPERATIONS + " AND " + filterQueryBuilder.getFilterQuery() + sqlStmtTail;
        }
        return GET_UNIT_OPERATIONS + sqlStmtTail;
    }

    private void setFilterAttributes(NamedPreparedStatement namedPreparedStatement,
                                     Map<String, String> filterAttributeValue, List<String> timestampTypeAttributes)
            throws SQLException {

        for (Map.Entry<String, String> entry : filterAttributeValue.entrySet()) {
            if (timestampTypeAttributes.contains(entry.getKey())) {
                namedPreparedStatement.setTimeStamp(entry.getKey(), Timestamp.valueOf(entry.getValue()), null);
            } else {
                namedPreparedStatement.setString(entry.getKey(), entry.getValue());
            }
        }
    }
}
