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

package org.wso2.carbon.claim.mgt;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.identity.claim.metadata.mgt.ClaimMetadataManagementServiceImpl;
import org.wso2.carbon.identity.claim.metadata.mgt.exception.ClaimMetadataException;
import org.wso2.carbon.identity.claim.metadata.mgt.model.ExternalClaim;
import org.wso2.carbon.user.api.Claim;
import org.wso2.carbon.user.api.ClaimMapping;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.config.RealmConfiguration;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;
import static org.wso2.carbon.user.core.UserCoreConstants.DEFAULT_CARBON_DIALECT;

/**
 * Tests for {@link ClaimManagerHandler} covering realm fallbacks and metadata integration.
 */
public class ClaimManagerHandlerTest {

    private UserRealm userRealm;
    private org.wso2.carbon.user.core.claim.ClaimManager claimManager;
    private RealmService realmService;
    private TenantManager tenantManager;
    private ClaimMetadataManagementServiceImpl metadataService;
    private RealmConfiguration realmConfiguration;

    private StubAdminWrapper adminWrapper;
    private StubComponentWrapper componentWrapper;
    private StubMetadataSupplier metadataSupplier;
    private ClaimManagerHandler handler;

    @BeforeMethod
    public void setUp() throws Exception {

        userRealm = mock(UserRealm.class);
        claimManager = mock(org.wso2.carbon.user.core.claim.ClaimManager.class);
        when(userRealm.getClaimManager()).thenReturn(claimManager);
        realmConfiguration = mock(RealmConfiguration.class);
        when(userRealm.getRealmConfiguration()).thenReturn(realmConfiguration);
        when(realmConfiguration.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME))
                .thenReturn(UserCoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME);

        realmService = mock(RealmService.class);
        tenantManager = mock(TenantManager.class);
        when(realmService.getTenantManager()).thenReturn(tenantManager);

        metadataService = mock(ClaimMetadataManagementServiceImpl.class);

        adminWrapper = new StubAdminWrapper(userRealm);
        componentWrapper = new StubComponentWrapper(realmService);
        metadataSupplier = new StubMetadataSupplier(metadataService);

        handler = ClaimManagerHandler.createForTesting(componentWrapper, adminWrapper, metadataSupplier);
    }

    @AfterMethod
    public void tearDown() {

        adminWrapper = null;
        componentWrapper = null;
        metadataSupplier = null;
        handler = null;
    }

    @Test
    public void testGetAllSupportedClaimsReturnsClaimsWhenManagerPresent() throws Exception {

        ClaimMapping mappingOne = mapping("uri1");
        ClaimMapping mappingTwo = mapping("uri2");
        when(claimManager.getAllSupportClaimMappingsByDefault())
                .thenReturn(new ClaimMapping[]{mappingOne, mappingTwo});

        Claim[] claims = handler.getAllSupportedClaims();

        assertEquals(claims.length, 2);
        assertEquals(claims[0].getClaimUri(), "uri1");
        assertEquals(claims[1].getClaimUri(), "uri2");
    }

    @Test
    public void testGetAllSupportedClaimsReturnsEmptyWhenNoManager() throws Exception {

        when(userRealm.getClaimManager()).thenReturn(null);

        Claim[] claims = handler.getAllSupportedClaims();
        assertNotNull(claims);
        assertEquals(claims.length, 0);
    }

    @Test
    public void testGetAllSupportedClaimsThrowsWhenRealmUnavailable() throws Exception {

        adminWrapper.failWith(new CarbonException("boom"));

        ClaimManagementException ex = expectThrows(ClaimManagementException.class,
                () -> handler.getAllSupportedClaims());
        assertTrue(ex.getMessage().contains("Error occurred while resolving user realm for loading supported claims"));
    }

    @Test
    public void testUpdateClaimMappingDelegatesToClaimManager() throws Exception {

        Claim claim = new Claim();
        claim.setClaimUri("uri");
        ClaimMapping mapping = new ClaimMapping(claim, "mapped");

        handler.updateClaimMapping(mapping);

        verify(claimManager, times(1)).updateClaimMapping(mapping);
    }

    @Test
    public void testUpdateClaimMappingWrapsRealmFailures() throws Exception {

        adminWrapper.failWith(new CarbonException("realm unavailable"));
        Claim claim = new Claim();
        claim.setClaimUri("uri");
        ClaimMapping mapping = new ClaimMapping(claim, "mapped");

        ClaimManagementException ex = expectThrows(ClaimManagementException.class,
                () -> handler.updateClaimMapping(mapping));
        assertTrue(ex.getMessage().contains("resolving user realm"));
    }

    @Test
    public void testGetMappingsMapFromOtherDialectToCarbonUsesMetadataService() throws Exception {

        ExternalClaim external = new ExternalClaim("external", "externalClaim", "carbonClaim");
        ClaimManagerHandler metadataAware = handlerWithMetadata(List.of(external));

        Map<String, String> map = metadataAware
                .getMappingsMapFromOtherDialectToCarbon("external", new HashSet<>(), "tenant");

        assertEquals(map.size(), 1);
        assertEquals(map.get("externalClaim"), "carbonClaim");
    }

    @Test
    public void testGetMappingsMapFromOtherDialectToCarbonWrapsMetadataErrors() throws Exception {

        ClaimManagerHandler failingMetadata = handlerWithMetadataFailure();

        ClaimManagementException ex = expectThrows(ClaimManagementException.class, () ->
                failingMetadata.getMappingsMapFromOtherDialectToCarbon("external", new HashSet<>(), "tenant"));

        assertTrue(ex.getMessage().contains("error"));
    }

    @Test
    public void testGetAllClaimMappingsByTenantFallsBackToEmptyArrayWhenManagerMissing() throws Exception {

        when(tenantManager.getTenantId("tenant"))
                .thenReturn(2);
        when(realmService.getTenantUserRealm(2)).thenReturn(userRealm);
        when(userRealm.getClaimManager()).thenReturn(null);

        ClaimMapping[] result = handler.getAllClaimMappings("dialect", "tenant");

        assertNotNull(result);
        assertEquals(result.length, 0);
    }

    private ClaimMapping mapping(String claimUri) {

        Claim claim = new Claim();
        claim.setClaimUri(claimUri);
        return new ClaimMapping(claim, "mapped");
    }

    private ClaimManagerHandler handlerWithMetadata(List<ExternalClaim> externalClaims)
            throws ClaimMetadataException {

        ClaimMetadataManagementServiceImpl metadata = mock(ClaimMetadataManagementServiceImpl.class);
        when(metadata.getExternalClaims(eq("external"), eq("tenant"))).thenReturn(externalClaims);
        return ClaimManagerHandler.createForTesting(componentWrapper, adminWrapper, new StubMetadataSupplier(metadata));
    }

    private ClaimManagerHandler handlerWithMetadataFailure() throws ClaimMetadataException {

        ClaimMetadataManagementServiceImpl metadata = mock(ClaimMetadataManagementServiceImpl.class);
        when(metadata.getExternalClaims(any(), any())).thenThrow(new ClaimMetadataException("error"));
        return ClaimManagerHandler.createForTesting(componentWrapper, adminWrapper, new StubMetadataSupplier(metadata));
    }
}

class StubAdminWrapper extends AdminServiceWrapper {

    private final UserRealm realm;
    private CarbonException failure;

    StubAdminWrapper(UserRealm realm) {

        this.realm = realm;
    }

    void failWith(CarbonException exception) {

        this.failure = exception;
    }

    @Override
    UserRealm getUserRealm() throws CarbonException {

        if (failure != null) {
            throw failure;
        }
        return realm;
    }
}

class StubComponentWrapper extends ClaimManagementServiceComponentWrapper {

    private final RealmService realmService;

    StubComponentWrapper(RealmService realmService) {

        this.realmService = realmService;
    }

    @Override
    RealmService getRealmService() {

        return realmService;
    }
}

class StubMetadataSupplier extends MetadataServiceSupplier {

    private final ClaimMetadataManagementServiceImpl service;

    StubMetadataSupplier(ClaimMetadataManagementServiceImpl service) {

        this.service = service;
    }

    @Override
    ClaimMetadataManagementServiceImpl getService() {

        return service;
    }
}
