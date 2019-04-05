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
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

/**
 * This is an integration test (extends BaseModuleContextSensitiveTest), which verifies logic in InsuranceClaimService.
 */
public class InsuranceClaimServiceTest extends BaseModuleContextSensitiveTest {

	@Autowired
	@Qualifier("insuranceclaims.InsuranceClaimService")
	private InsuranceClaimService insuranceClaimService;

	@Test
	public void saveItem_shouldCorrectlySaveClaim() {
		InsuranceClaim claim = createTestInstance();
		insuranceClaimService.saveOrUpdate(claim);
		InsuranceClaim savedClaim = insuranceClaimService.getByUuid(claim.getUuid());

		Assert.assertThat(savedClaim, hasProperty("uuid", is(claim.getUuid())));
		Assert.assertThat(savedClaim, hasProperty("provider", is(claim.getProvider())));
		Assert.assertThat(savedClaim, hasProperty("patient", is(claim.getPatient())));
		Assert.assertThat(savedClaim, hasProperty("location", is(claim.getLocation())));
		Assert.assertThat(savedClaim, hasProperty("claimCode", is(claim.getClaimCode())));
		Assert.assertThat(savedClaim, hasProperty("dateFrom", is(claim.getDateFrom())));
		Assert.assertThat(savedClaim, hasProperty("dateTo", is(claim.getDateTo())));
		Assert.assertThat(savedClaim, hasProperty("adjustment", is(claim.getAdjustment())));
		Assert.assertThat(savedClaim, hasProperty("claimedTotal", is(claim.getClaimedTotal())));
		Assert.assertThat(savedClaim, hasProperty("approvedTotal", is(claim.getApprovedTotal())));
		Assert.assertThat(savedClaim, hasProperty("dateProcessed", is(claim.getDateProcessed())));
		Assert.assertThat(savedClaim, hasProperty("explanation", is(claim.getExplanation())));
		Assert.assertThat(savedClaim, hasProperty("rejectionReason", is(claim.getRejectionReason())));
		Assert.assertThat(savedClaim, hasProperty("guaranteeId", is(claim.getGuaranteeId())));
		Assert.assertThat(savedClaim, hasProperty("visitType", is(claim.getVisitType())));
		Assert.assertThat(savedClaim, hasProperty("claimStatus", is(claim.getClaimStatus())));
	}

	@Test
	public void getService_shouldReturnNotNullService() {
		InsuranceClaimService insuranceClaimService = Context.getService(InsuranceClaimService.class);
		Assert.assertNotNull(insuranceClaimService);
	}

	private InsuranceClaim createTestInstance() {
		Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
		Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
		VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
		PatientIdentifierType identifierType = Context.getPatientService()
				.getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
		return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
	}
}
