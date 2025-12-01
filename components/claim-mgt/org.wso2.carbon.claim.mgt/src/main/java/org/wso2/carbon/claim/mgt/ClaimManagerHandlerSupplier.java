package org.wso2.carbon.claim.mgt;

class ClaimManagerHandlerSupplier {

    ClaimManagerHandler getHandler() {

        return ClaimManagerHandler.getInstance();
    }
}

