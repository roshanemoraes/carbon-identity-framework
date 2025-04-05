package org.wso2.carbon.identity.framework.async.status.mgt.internal;

import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.framework.async.status.mgt.dao.AsyncStatusMgtDAO;

import javax.sql.DataSource;

/**
 * Data holder for asynchronous operation status management.
 */
public class AsyncStatusMgtDataHolder {

    private DataSource dataSource;
    private static final AsyncStatusMgtDataHolder INSTANCE = new AsyncStatusMgtDataHolder();

    private AsyncStatusMgtDataHolder() {

    }

    /**
     * Get the instance of AsyncStatusMgtDataHolder.
     *
     * @return AsyncStatusMgtDataHolder instance.
     */
    public static AsyncStatusMgtDataHolder getInstance() {

        return INSTANCE;
    }

    /**
     * Get the instance of Datasource.
     *
     * @return DataSource instance.
     */
    public DataSource getDataSource(){
        if (dataSource == null){
            this.dataSource = IdentityDatabaseUtil.getDataSource();
        }
        return dataSource;
    }
}
