package org.wso2.carbon.identity.framework.async.status.mgt.util;

import org.apache.commons.lang.ArrayUtils;
import org.wso2.carbon.database.utils.jdbc.NamedJdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.identity.framework.async.status.mgt.constant.AsyncStatusMgtConstants;
import org.wso2.carbon.identity.framework.async.status.mgt.exception.AsyncStatusMgtClientException;
import org.wso2.carbon.identity.framework.async.status.mgt.exception.AsyncStatusMgtServerException;
import org.wso2.carbon.identity.framework.async.status.mgt.internal.AsyncStatusMgtDataHolder;

import javax.sql.DataSource;

public class Utils {
//    private static DataSource dataSource;

    private static boolean isDBTypeOf(String dbType) throws AsyncStatusMgtServerException {
        try {
            NamedJdbcTemplate jdbcTemplate = getNewTemplate();
            return jdbcTemplate.getDriverName().toLowerCase().contains(dbType) || jdbcTemplate.getDatabaseProductName().toLowerCase().contains(dbType);
        } catch (DataAccessException e) {
            throw handleServerException(AsyncStatusMgtConstants.ErrorMessages.ERROR_CODE_INVALID_REQUEST_BODY, e);
        }
    }

    public static boolean isMSSqlDB() throws AsyncStatusMgtServerException {
        return isDBTypeOf("microsoft");
    }

    /**
     * Get a new Jdbc Template.
     *
     * @return a new Jdbc Template.
     */
    public static NamedJdbcTemplate getNewTemplate() {
        return new NamedJdbcTemplate(AsyncStatusMgtDataHolder.getInstance().getDataSource());
    }

    /**
     * Throw an OrganizationManagementClientException upon client side error in organization management.
     *
     * @param error The error enum.
     * @param data  The error message data.
     * @return OrganizationManagementClientException
     */
    public static AsyncStatusMgtClientException handleClientException(
            AsyncStatusMgtConstants.ErrorMessages error, String... data) {

        String description = error.getDescription();
        if (ArrayUtils.isNotEmpty(data)) {
            description = String.format(description, data);
        }
        return new AsyncStatusMgtClientException(error.getMessage(), description, error.getCode());
    }

    /**
     * Throw an AsyncStatusMgtServerException upon server side error in organization management.
     *
     * @param error The error enum.
     * @param e     The error.
     * @param data  The error message data.
     * @return AsyncStatusMgtServerException
     */
    public static AsyncStatusMgtServerException handleServerException(
            AsyncStatusMgtConstants.ErrorMessages error, Throwable e, String... data) {

        String description = error.getDescription();
        if (ArrayUtils.isNotEmpty(data)) {
            description = String.format(description, data);
        }
        return new AsyncStatusMgtServerException(error.getMessage(), description, error.getCode(), e);
    }

}
