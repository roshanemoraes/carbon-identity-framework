package org.wso2.carbon.claim.mgt;

import org.wso2.carbon.CarbonException;
import org.wso2.carbon.core.util.AdminServicesUtil;
import org.wso2.carbon.user.core.UserRealm;

class AdminServiceWrapper {

    UserRealm getUserRealm() throws CarbonException {

        return AdminServicesUtil.getUserRealm();
    }
}

