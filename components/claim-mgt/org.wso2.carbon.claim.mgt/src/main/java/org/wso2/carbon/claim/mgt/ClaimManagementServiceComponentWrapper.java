package org.wso2.carbon.claim.mgt;

import org.wso2.carbon.claim.mgt.internal.ClaimManagementServiceComponent;
import org.wso2.carbon.user.core.service.RealmService;

class ClaimManagementServiceComponentWrapper {

    RealmService getRealmService() {

        return ClaimManagementServiceComponent.getRealmService();
    }
}

