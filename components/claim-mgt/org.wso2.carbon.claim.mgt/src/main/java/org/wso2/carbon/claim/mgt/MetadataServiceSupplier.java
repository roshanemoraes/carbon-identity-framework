package org.wso2.carbon.claim.mgt;

import org.wso2.carbon.identity.claim.metadata.mgt.ClaimMetadataManagementServiceImpl;

class MetadataServiceSupplier {

    ClaimMetadataManagementServiceImpl getService() {

        return new ClaimMetadataManagementServiceImpl();
    }
}

