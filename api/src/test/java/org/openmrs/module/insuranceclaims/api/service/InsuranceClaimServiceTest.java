package org.openmrs.module.insuranceclaims.api.service;

import org.junit.Assert;
import org.junit.Test;
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

	@Autowired
	@Qualifier("insuranceclaims.InsuranceClaimService")
	private InsuranceClaimService insuranceClaimService;

	@Test
	public void saveItem_shouldCorrectlySaveClaim() {
		InsuranceClaim claim = InsuranceClaimMother.createTestInstance();
		insuranceClaimService.saveInsuranceClaim(claim);
		InsuranceClaim savedClaim = insuranceClaimService.getInsuranceClaimByUuid(claim.getUuid());

		Assert.assertEquals(claim, savedClaim);
	}

	@Test
	public void verifyIfServiceCanBeAccessedViaContext() {
		InsuranceClaimService insuranceClaimService = Context.getService(InsuranceClaimService.class);
		Assert.assertNotNull(insuranceClaimService);
	}
}
