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
import org.wso2.carbon.claim.mgt.dto.ClaimAttributeDTO;
import org.wso2.carbon.claim.mgt.dto.ClaimDTO;
import org.wso2.carbon.claim.mgt.dto.ClaimDialectDTO;
import org.wso2.carbon.claim.mgt.dto.ClaimMappingDTO;
import org.wso2.carbon.user.api.Claim;
import org.wso2.carbon.user.api.ClaimMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link ClaimAdminService}.
 */
public class ClaimAdminServiceTest {

    private ClaimAdminService claimAdminService;
    private ClaimManagerHandler handlerMock;

    @BeforeMethod
    public void setUp() throws Exception {

        handlerMock = mock(ClaimManagerHandler.class);
        claimAdminService = new ClaimAdminService(new ClaimManagerHandlerSupplier() {
            @Override
            public ClaimManagerHandler getHandler() {

                return handlerMock;
            }
        });
    }

    @AfterMethod
    public void tearDown() {

        // No-op cleanup placeholder retained intentionally.
    }

    @Test
    public void testGetClaimMappingsGroupsByDialect() throws Exception {

        ClaimMapping dialectAClaim1 = buildClaimMapping("dialectA", "Description B", "claimA1");
        ClaimMapping dialectAClaim2 = buildClaimMapping("dialectA", "Description A", "claimA2");
        ClaimMapping dialectBClaim = buildClaimMapping("dialectB", "Another Claim", "claimB1");

        when(handlerMock.getAllClaimMappings())
                .thenReturn(new ClaimMapping[]{dialectAClaim1, dialectAClaim2, dialectBClaim});

        ClaimDialectDTO[] response = claimAdminService.getClaimMappings();

        assertEquals(response.length, 2, "Two dialects expected");

        ClaimDialectDTO dialectA = findDialect(response, "dialectA");
        ClaimDialectDTO dialectB = findDialect(response, "dialectB");

        assertNotNull(dialectA, "dialectA should be present");
        assertNotNull(dialectB, "dialectB should be present");

        ClaimMappingDTO[] dialectAClaims = dialectA.getClaimMappings();
        assertEquals(dialectAClaims.length, 2, "dialectA should have two claims");
        assertEquals(dialectAClaims[0].getClaim().getDescription(), "Description A",
                "Descriptions must be sorted alphabetically");
        assertEquals(dialectAClaims[1].getClaim().getDescription(), "Description B");

        ClaimMappingDTO[] dialectBClaims = dialectB.getClaimMappings();
        assertEquals(dialectBClaims.length, 1);
        assertEquals(dialectBClaims[0].getClaim().getClaimUri(), "claimB1");
    }

    @Test
    public void testGetClaimMappingsReturnsEmptyWhenNoClaims() throws Exception {

        when(handlerMock.getAllClaimMappings()).thenReturn(new ClaimMapping[0]);

        ClaimDialectDTO[] response = claimAdminService.getClaimMappings();
        assertNotNull(response);
        assertEquals(response.length, 0, "Expect empty response when handler returns nothing");
    }

    @Test
    public void testGetClaimMappingByDialectSortsByDescription() throws Exception {

        ClaimMapping mappingOne = buildClaimMapping("dialectA", "B trait", "claimA1");
        ClaimMapping mappingTwo = buildClaimMapping("dialectA", "A trait", "claimA2");

        when(handlerMock.getAllSupportedClaimMappings("dialectA"))
                .thenReturn(new ClaimMapping[]{mappingOne, mappingTwo});

        ClaimDialectDTO result = claimAdminService.getClaimMappingByDialect("dialectA");

        assertNotNull(result);
        assertEquals(result.getDialectURI(), "dialectA");
        List<String> descriptions = Arrays.stream(result.getClaimMappings())
                .map(dto -> dto.getClaim().getDescription())
                .collect(Collectors.toList());
        assertEquals(descriptions, Arrays.asList("A trait", "B trait"));
    }

    @Test
    public void testGetClaimMappingByDialectReturnsNullWhenEmpty() throws Exception {

        when(handlerMock.getAllSupportedClaimMappings("dialectA"))
                .thenReturn(new ClaimMapping[0]);

        ClaimDialectDTO result = claimAdminService.getClaimMappingByDialect("dialectA");
        assertNull(result);
    }

    @Test(expectedExceptions = ClaimManagementException.class,
            expectedExceptionsMessageRegExp = "Duplicate claim exist.*")
    public void testAddNewClaimMappingRejectsDuplicates() throws Exception {

        ClaimMappingDTO dto = sampleClaimMappingDTO();

        ClaimMapping existing = invokeConvertToClaimMapping(dto);
        when(handlerMock.getClaimMapping(existing.getClaim().getClaimUri())).thenReturn(existing);

        claimAdminService.addNewClaimMapping(dto);
    }

    @Test
    public void testAddNewClaimMappingDelegatesToHandler() throws Exception {

        ClaimMappingDTO dto = sampleClaimMappingDTO();

        when(handlerMock.getClaimMapping(dto.getClaim().getClaimUri())).thenReturn(null);

        claimAdminService.addNewClaimMapping(dto);

        verify(handlerMock, times(1)).addNewClaimMapping(any(ClaimMapping.class));
    }

    @Test
    public void testUpdateClaimMappingDelegatesToHandler() throws Exception {

        ClaimMappingDTO dto = sampleClaimMappingDTO();

        claimAdminService.upateClaimMapping(dto);

        verify(handlerMock, times(1)).updateClaimMapping(any(ClaimMapping.class));
    }

    @Test
    public void testRemoveClaimMappingDelegatesToHandler() throws Exception {

        claimAdminService.removeClaimMapping("dialect", "claimUri");

        verify(handlerMock, times(1)).removeClaimMapping("dialect", "claimUri");
    }

    @Test
    public void testAddNewClaimDialectDelegatesToHandler() throws Exception {

        ClaimDialectDTO claimDialectDTO = new ClaimDialectDTO();
        claimDialectDTO.setDialectURI("dialect");
        claimDialectDTO.setClaimMappings(new ClaimMappingDTO[]{sampleClaimMappingDTO()});

        claimAdminService.addNewClaimDialect(claimDialectDTO);

        verify(handlerMock, times(1)).addNewClaimDialect(any(ClaimDialect.class));
    }

    @Test
    public void testRemoveClaimDialectDelegatesToHandler() throws Exception {

        claimAdminService.removeClaimDialect("dialect");

        verify(handlerMock, times(1)).removeClaimDialect("dialect");
    }

    @Test
    public void testConvertClaimMappingDTOToClaimMappingCopiesMappedAttributes() throws Exception {

        ClaimAttributeDTO attribute1 = buildAttribute("DOMAIN_1", "attrOne");
        ClaimAttributeDTO attribute2 = buildAttribute("DOMAIN_2", "attrTwo");

        ClaimMappingDTO dto = sampleClaimMappingDTO();
        dto.setMappedAttributes(new ClaimAttributeDTO[]{attribute1, attribute2});

        ClaimMapping domainConverted = invokeConvertToClaimMapping(dto);
        assertEquals(domainConverted.getMappedAttribute("DOMAIN_1"), "attrOne");
        assertEquals(domainConverted.getMappedAttribute("DOMAIN_2"), "attrTwo");
    }

    @Test
    public void testConvertClaimMappingToDTOCopiesMappedAttributes() throws Exception {

        ClaimMapping original = buildClaimMapping("dialect", "Description", "claimUri");
        Map<String, String> mapped = new HashMap<>();
        mapped.put("DOMAIN_A", "attributeA");
        mapped.put("DOMAIN_B", "attributeB");
        original.setMappedAttributes(mapped);

        ClaimMappingDTO dto = invokeConvertToDTO(original);

        ClaimAttributeDTO[] attributes = dto.getMappedAttributes();
        assertNotNull(attributes);
        assertEquals(attributes.length, 2);
        assertTrue(Arrays.stream(attributes).anyMatch(attr ->
                "DOMAIN_A".equals(attr.getDomainName()) && "attributeA".equals(attr.getAttributeName())));
        assertTrue(Arrays.stream(attributes).anyMatch(attr ->
                "DOMAIN_B".equals(attr.getDomainName()) && "attributeB".equals(attr.getAttributeName())));
    }

    @Test
    public void testConvertClaimMappingArrayRoundTrip() throws Exception {

        ClaimMappingDTO dto = sampleClaimMappingDTO();
        ClaimMappingDTO[] array = new ClaimMappingDTO[]{dto};

        ClaimMapping[] converted = invokeConvertDTOArray(array);
        ClaimMappingDTO[] convertedBack = invokeConvertArrayToDTO(converted);

        assertEquals(convertedBack.length, 1);
        assertEquals(convertedBack[0].getClaim().getClaimUri(), dto.getClaim().getClaimUri());
    }

    @Test
    public void testGetClaimMappingsHandlesNullHandlerResponse() throws Exception {

        when(handlerMock.getAllClaimMappings()).thenReturn(null);

        ClaimDialectDTO[] response = claimAdminService.getClaimMappings();
        assertNotNull(response);
        assertEquals(response.length, 0, "Should guard against null handler response");
    }

    private ClaimMappingDTO sampleClaimMappingDTO() {

        ClaimDTO claimDTO = new ClaimDTO();
        claimDTO.setClaimUri("http://wso2.org/claims/sample");
        claimDTO.setDescription("Sample Claim");
        claimDTO.setDialectURI("http://wso2.org/claims");
        claimDTO.setDisplayOrder(1);
        claimDTO.setDisplayTag("Sample Tag");
        claimDTO.setRegEx(".*");
        claimDTO.setRequired(false);
        claimDTO.setSupportedByDefault(true);
        claimDTO.setValue("value");
        claimDTO.setCheckedAttribute(true);
        claimDTO.setReadOnly(false);

        ClaimMappingDTO mappingDTO = new ClaimMappingDTO();
        mappingDTO.setClaim(claimDTO);
        mappingDTO.setMappedAttribute("uid");
        mappingDTO.setMappedAttributes(new ClaimAttributeDTO[0]);
        return mappingDTO;
    }

    private ClaimMapping buildClaimMapping(String dialectUri, String description, String claimUri) {

        Claim claim = new Claim();
        claim.setDialectURI(dialectUri);
        claim.setDescription(description);
        claim.setClaimUri(claimUri);

        ClaimMapping mapping = new ClaimMapping(claim, "mappedAttribute");
        mapping.setMappedAttributes(new HashMap<>());
        return mapping;
    }

    private ClaimAttributeDTO buildAttribute(String domain, String name) {

        ClaimAttributeDTO dto = new ClaimAttributeDTO();
        dto.setDomainName(domain);
        dto.setAttributeName(name);
        return dto;
    }

    private ClaimDialectDTO findDialect(ClaimDialectDTO[] dialects, String uri) {

        return Arrays.stream(dialects)
                .filter(dto -> uri.equals(dto.getDialectURI()))
                .findFirst()
                .orElse(null);
    }

    private ClaimMapping invokeConvertToClaimMapping(ClaimMappingDTO dto) throws Exception {

        java.lang.reflect.Method method = ClaimAdminService.class
                .getDeclaredMethod("convertClaimMappingDTOToClaimMapping", ClaimMappingDTO.class);
        method.setAccessible(true);
        return (ClaimMapping) method.invoke(claimAdminService, dto);
    }

    private ClaimMappingDTO invokeConvertToDTO(ClaimMapping mapping) throws Exception {

        java.lang.reflect.Method method = ClaimAdminService.class
                .getDeclaredMethod("convertClaimMappingToClaimMappingDTO", ClaimMapping.class);
        method.setAccessible(true);
        return (ClaimMappingDTO) method.invoke(claimAdminService, mapping);
    }

    private ClaimMapping[] invokeConvertDTOArray(ClaimMappingDTO[] dtos) throws Exception {

        java.lang.reflect.Method method = ClaimAdminService.class
                .getDeclaredMethod("convertClaimMappingDTOArrayToClaimMappingArray", ClaimMappingDTO[].class);
        method.setAccessible(true);
        return (ClaimMapping[]) method.invoke(claimAdminService, new Object[]{dtos});
    }

    private ClaimMappingDTO[] invokeConvertArrayToDTO(ClaimMapping[] mappings) throws Exception {

        java.lang.reflect.Method method = ClaimAdminService.class
                .getDeclaredMethod("convertClaimMappingArrayToClaimMappingDTOArray", ClaimMapping[].class);
        method.setAccessible(true);
        return (ClaimMappingDTO[]) method.invoke(claimAdminService, new Object[]{mappings});
    }
}
