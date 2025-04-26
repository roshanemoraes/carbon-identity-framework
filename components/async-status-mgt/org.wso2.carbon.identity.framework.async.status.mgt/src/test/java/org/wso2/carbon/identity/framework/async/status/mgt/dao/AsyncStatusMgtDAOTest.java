package org.wso2.carbon.identity.framework.async.status.mgt.dao;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.common.testng.WithCarbonHome;
import org.wso2.carbon.identity.common.testng.WithH2Database;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.framework.async.status.mgt.api.exception.AsyncStatusMgtException;
import org.wso2.carbon.identity.framework.async.status.mgt.api.models.OperationInitDTO;
import org.wso2.carbon.identity.framework.async.status.mgt.api.models.UnitOperationInitDTO;
import org.wso2.carbon.identity.framework.async.status.mgt.internal.dao.AsyncStatusMgtDAO;
import org.wso2.carbon.identity.framework.async.status.mgt.internal.dao.impl.AsyncStatusMgtDAOImpl;
import org.wso2.carbon.identity.framework.async.status.mgt.internal.models.dos.OperationDO;
import org.wso2.carbon.identity.framework.async.status.mgt.internal.models.dos.UnitOperationDO;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.CORR_ID_1;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.CORR_ID_2;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.INITIATOR_ID_1;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.POLICY_SELECTIVE_SHARE;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.RESIDENT_ORG_ID_1;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.RESIDENT_ORG_ID_3;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.RESIDENT_ORG_ID_4;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.STATUS_FAIL;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.STATUS_ONGOING;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.STATUS_PARTIAL;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.STATUS_SUCCESS;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.SUBJECT_ID_1;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.SUBJECT_TYPE_USER;
import static org.wso2.carbon.identity.framework.async.status.mgt.constants.TestAsyncOperationConstants.TYPE_USER_SHARE;

@Test
@WithH2Database(jndiName = "jdbc/WSO2IdentityDB",
        files = { "dbScripts/async_operation_status.sql" })
@WithCarbonHome
public class AsyncStatusMgtDAOTest {

    private AsyncStatusMgtDAO dao;

    @BeforeClass
    public void initTest() throws Exception {

        dao = new AsyncStatusMgtDAOImpl();
    }

    @BeforeMethod
    public void setUp() throws Exception {

        cleanUpDB();
    }

    @DataProvider(name = "asyncOperationDetailProvider")
    public Object[][] asyncOperationDetailProvider() throws Exception {

        return new Object[][] {
                {new OperationInitDTO(
                        "56565656565655",
                        TYPE_USER_SHARE,
                        "B2B_APPLICATION",
                        "23d7ab3f-023e-43ba-980b-c0fd59aeacf9",
                        "10084a8d-113f-4211-a0d5-efe36b082211",
                        "53c191dd-3f9f-454b-8a56-9ad72b5e4f30",
                        "SHARE_WITH_ALL"
                )},
        };
    }

    @Test(priority = 1)
    public void testRegisterOperationWithoutUpdateSuccess() {

        try {
            OperationInitDTO
                    operation1 = new OperationInitDTO(CORR_ID_1, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);

            OperationInitDTO
                    operation2 = new OperationInitDTO(CORR_ID_2, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);

            String insertedOperationId1 = dao.registerAsyncStatusWithoutUpdate(operation1);
            assertTrue(StringUtils.isNotBlank(insertedOperationId1), "Async Op_1 Status Addition Failed.");
            assertTrue(Integer.parseInt(insertedOperationId1) > 0, "Expected a positive non-zero " +
                    "integer as the result for a clean record insertion to the database.");

            dao.registerAsyncStatusWithoutUpdate(operation2);
            assertEquals(2, getOperationTableSize());
        } catch (AsyncStatusMgtException e) {
            Assert.fail();
        }
    }

    @Test(dataProvider = "asyncOperationDetailProvider", priority = 2)
    public void testRegisterOperationWithUpdateSuccess(OperationInitDTO testData) {

        try {
            OperationInitDTO
                    operation1 = new OperationInitDTO(CORR_ID_1, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);

            OperationInitDTO
                    operation2 = new OperationInitDTO(CORR_ID_2, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);

            String insertedOperationId1 = dao.registerAsyncStatusWithUpdate(operation1);
            assertTrue(StringUtils.isNotBlank(insertedOperationId1), "Async Op_1 Status Addition Failed.");
            assertTrue(Integer.parseInt(insertedOperationId1) > 0, "Expected a positive non-zero " +
                    "integer as the result for a clean record insertion to the database.");

            dao.registerAsyncStatusWithUpdate(operation2);
            assertEquals(1, getOperationTableSize());
        } catch (AsyncStatusMgtException e) {
            Assert.fail();
        }
    }

    @Test(priority = 3)
    public void testUpdateAsyncStatus() {

        try {
            OperationInitDTO
                    operation1 = new OperationInitDTO(CORR_ID_1, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);

            String initialOperationId = dao.registerAsyncStatusWithUpdate(operation1);

            OperationDO fetchedOperation = dao.getOperations(RESIDENT_ORG_ID_1, 1000, null).get(0);
            String initialStatus = fetchedOperation.getOperationStatus();
            assertEquals(STATUS_ONGOING, initialStatus);

            dao.updateAsyncStatus(initialOperationId, STATUS_SUCCESS);
            assertEquals(1, getOperationTableSize());

            String fetchedUpdatedStatus = dao.getOperations(RESIDENT_ORG_ID_1, 1000,
                    null).get(0).getOperationStatus();
            assertEquals(STATUS_SUCCESS, fetchedUpdatedStatus);
        } catch (AsyncStatusMgtException e) {
            Assert.fail();
        }
    }

    @Test(priority = 4)
    public void testRegisterAsyncStatusUnit() {

        try {
            OperationInitDTO
                    operation1 = new OperationInitDTO(CORR_ID_1, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);
            String returnedId = dao.registerAsyncStatusWithUpdate(operation1);
            String fetchedOperationId = dao.getOperations(RESIDENT_ORG_ID_1, 1000,
                    null).get(0).getOperationId();

            UnitOperationInitDTO unit1 = new UnitOperationInitDTO(returnedId, RESIDENT_ORG_ID_1,
                    RESIDENT_ORG_ID_4, STATUS_SUCCESS, StringUtils.EMPTY);
            UnitOperationInitDTO unit2 = new UnitOperationInitDTO(returnedId, RESIDENT_ORG_ID_1,
                    RESIDENT_ORG_ID_3, STATUS_FAIL, "Invalid User Id.");
            ConcurrentLinkedQueue<UnitOperationInitDTO> list = new ConcurrentLinkedQueue<>();
            list.add(unit1);
            list.add(unit2);
            dao.registerAsyncStatusUnit(list);
            dao.updateAsyncStatus(returnedId, STATUS_PARTIAL);

            assertEquals(2, dao.getUnitOperations(fetchedOperationId, RESIDENT_ORG_ID_1,
                    10, null).size());
        } catch (AsyncStatusMgtException e) {
            Assert.fail();
        }
    }

    @Test(priority = 5)
    public void testGetOperationRecords() {

        try {
            OperationInitDTO
                    operation1 = new OperationInitDTO(CORR_ID_1, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);
            OperationInitDTO
                    operation2 = new OperationInitDTO(CORR_ID_2, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);

            assertEquals(0, dao.getOperations(RESIDENT_ORG_ID_1, 100, null).size());

            dao.registerAsyncStatusWithoutUpdate(operation1);
            dao.registerAsyncStatusWithoutUpdate(operation2);
            assertEquals(2, dao.getOperations(RESIDENT_ORG_ID_1, 100, null).size());

        } catch (AsyncStatusMgtException e) {
            Assert.fail();
        }
    }

    @Test(priority = 6)
    public void testGetOperation() {

        try {
            OperationInitDTO
                    operation1 = new OperationInitDTO(CORR_ID_1, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);

            dao.registerAsyncStatusWithoutUpdate(operation1);
            String fetchedOperationId = dao.getOperations(RESIDENT_ORG_ID_1, 100,
                    null).get(0).getOperationId();
            OperationDO record = dao.getOperation(fetchedOperationId, RESIDENT_ORG_ID_1);

            assertEquals(TYPE_USER_SHARE, record.getOperationType());
            assertEquals(SUBJECT_ID_1, record.getOperationSubjectId());
            assertEquals(RESIDENT_ORG_ID_1, record.getResidentOrgId());
            assertEquals(INITIATOR_ID_1, record.getInitiatorId());
            assertEquals(POLICY_SELECTIVE_SHARE, record.getOperationPolicy());
        } catch (AsyncStatusMgtException e) {
            Assert.fail();
        }
    }

    @Test(priority = 7)
    public void testGetUnitOperations() {

        try {
            OperationInitDTO
                    operation1 = new OperationInitDTO(CORR_ID_1, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);
            String returnedId = dao.registerAsyncStatusWithUpdate(operation1);
            String fetchedOperationId = dao.getOperations(RESIDENT_ORG_ID_1, 1000,
                    null).get(0).getOperationId();

            UnitOperationInitDTO unit1 = new UnitOperationInitDTO(returnedId, RESIDENT_ORG_ID_1,
                    RESIDENT_ORG_ID_4, STATUS_SUCCESS, StringUtils.EMPTY);
            UnitOperationInitDTO unit2 = new UnitOperationInitDTO(returnedId, RESIDENT_ORG_ID_1,
                    RESIDENT_ORG_ID_3, STATUS_FAIL, "Invalid User Id.");
            ConcurrentLinkedQueue<UnitOperationInitDTO> list = new ConcurrentLinkedQueue<>();
            list.add(unit1);
            list.add(unit2);
            dao.registerAsyncStatusUnit(list);
            dao.updateAsyncStatus(returnedId, STATUS_PARTIAL);

            assertEquals(2, dao.getUnitOperations(fetchedOperationId, RESIDENT_ORG_ID_1,
                    100, null).size());
        } catch (AsyncStatusMgtException e) {
            Assert.fail();
        }
    }

    @Test(priority = 8)
    public void testGetUnitOperation() {

        try {
            OperationInitDTO
                    operation1 = new OperationInitDTO(CORR_ID_1, TYPE_USER_SHARE, SUBJECT_TYPE_USER, SUBJECT_ID_1,
                    RESIDENT_ORG_ID_1, INITIATOR_ID_1, POLICY_SELECTIVE_SHARE);
            String returnedId = dao.registerAsyncStatusWithUpdate(operation1);
            String fetchedOperationId = dao.getOperations(RESIDENT_ORG_ID_1, 1000,
                    null).get(0).getOperationId();

            UnitOperationInitDTO unit1 = new UnitOperationInitDTO(returnedId, RESIDENT_ORG_ID_1,
                    RESIDENT_ORG_ID_4, STATUS_SUCCESS, StringUtils.EMPTY);
            ConcurrentLinkedQueue<UnitOperationInitDTO> list = new ConcurrentLinkedQueue<>();
            list.add(unit1);
            dao.registerAsyncStatusUnit(list);
            dao.updateAsyncStatus(returnedId, STATUS_PARTIAL);

            String addedUnitOpId = dao.getUnitOperations(fetchedOperationId, RESIDENT_ORG_ID_1,
                    100, null).get(0).getUnitOperationId();

            UnitOperationDO record = dao.getUnitOperation(addedUnitOpId, RESIDENT_ORG_ID_1);
            assertEquals(returnedId, record.getOperationId());
            assertEquals(RESIDENT_ORG_ID_1, record.getOperationInitiatedResourceId());
            assertEquals(RESIDENT_ORG_ID_4, record.getTargetOrgId());
            assertEquals(STATUS_SUCCESS, record.getUnitOperationStatus());
            assertEquals(StringUtils.EMPTY, record.getStatusMessage());

        } catch (AsyncStatusMgtException e) {
            Assert.fail();
        }
    }

    private void cleanUpDB() throws Exception {
        try (Connection connection = IdentityDatabaseUtil.getDBConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM IDN_ASYNC_OPERATION_STATUS");
                statement.executeUpdate("DELETE FROM IDN_ASYNC_OPERATION_STATUS_UNIT");
            }
            connection.commit();
        }
    }

    private int getOperationTableSize() throws AsyncStatusMgtException {
        return dao.getOperations(RESIDENT_ORG_ID_1, 1000, null).size();
    }

}
