package org.openmrs.module.insuranceclaims.api.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This is an integration test (extends BaseModuleContextSensitiveTest), which verifies logic in InsuranceClaimService.
 */
public class InsuranceClaimServiceTest extends BaseModuleContextSensitiveTest {

	private static final int TEST_LOCATION_ID = 1;

	private static final int TEST_PROVIDER_ID = 1;

	private static final int TEST_VISIT_TYPE_ID = 1;

	private static final int TEST_IDENTIFIER_TYPE_ID = 2;

	@Autowired
	@Qualifier("insuranceclaims.InsuranceClaimService")
	private InsuranceClaimService insuranceClaimService;

	@Test
	public void saveItem_shouldCorrectlySaveClaim() {
		InsuranceClaim claim = createTestInstance();
		insuranceClaimService.saveOrUpdate(claim);
		InsuranceClaim savedClaim = insuranceClaimService.getByUuid(claim.getUuid());

		Assert.assertEquals(claim, savedClaim);
	}

	@Test
	public void getService_shouldReturnNotNullService() {
		InsuranceClaimService insuranceClaimService = Context.getService(InsuranceClaimService.class);
		Assert.assertNotNull(insuranceClaimService);
	}

	private InsuranceClaim createTestInstance() {
		Location location = Context.getLocationService().getLocation(TEST_LOCATION_ID);
		Provider provider = Context.getProviderService().getProvider(TEST_PROVIDER_ID);
		VisitType visitType = Context.getVisitService().getVisitType(TEST_VISIT_TYPE_ID);
		PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(TEST_IDENTIFIER_TYPE_ID);
		return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
	}
}
