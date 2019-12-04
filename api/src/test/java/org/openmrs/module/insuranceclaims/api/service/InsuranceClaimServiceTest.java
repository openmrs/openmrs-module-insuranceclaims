package org.openmrs.module.insuranceclaims.api.service;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimDiagnosisMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimItemMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * This is an integration test (extends BaseModuleContextSensitiveTest), which verifies logic in InsuranceClaimService.
 */
public class InsuranceClaimServiceTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private InsuranceClaimService insuranceClaimService;

	@Autowired
	private InsuranceClaimItemService insuranceClaimItemService;

	@Autowired
	private InsuranceClaimDiagnosisService insuranceClaimDiagnosisService;

	@Test
	public void saveInsuranceClaim_shouldCorrectlySaveClaim() {
		InsuranceClaim claim = createTestClaim();
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
		Assert.assertThat(savedClaim, hasProperty("status", is(claim.getStatus())));
	}

	@Test
	public void getService_shouldReturnNotNullService() {
		InsuranceClaimService insuranceClaimService = Context.getService(InsuranceClaimService.class);
		Assert.assertThat(insuranceClaimService, is(notNullValue()));
	}

	@Test
	public void saveClaimItem_shouldCorrectlySaveClaim() {
		InsuranceClaimItem item = createTestClaimItem();
		insuranceClaimItemService.saveOrUpdate(item);
		InsuranceClaimItem savedClaim = insuranceClaimItemService.getByUuid(item.getUuid());

		Assert.assertThat(savedClaim, Matchers.notNullValue());
	}

	@Test
	public void getItemService_shouldReturnNotNullService() {
		InsuranceClaimItemService itemService = Context.getService(InsuranceClaimItemService.class);
		Assert.assertThat(itemService, is(notNullValue()));
	}

	@Test
	public void saveClaimDiagnosis_shouldCorrectlySaveClaim() {
		InsuranceClaimDiagnosis diagnosis = createTestClaimDiagnosis();
		insuranceClaimDiagnosisService.saveOrUpdate(diagnosis);
		InsuranceClaimDiagnosis savedClaim = insuranceClaimDiagnosisService.getByUuid(diagnosis.getUuid());

		Assert.assertThat(savedClaim, Matchers.notNullValue());
	}

	@Test
	public void getDiagnosisService_shouldReturnNotNullService() {
		InsuranceClaimDiagnosisService service = Context.getService(InsuranceClaimDiagnosisService.class);
		Assert.assertThat(service, is(notNullValue()));
	}

	private InsuranceClaim createTestClaim() {
		Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
		Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
		VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
		PatientIdentifierType identifierType = Context.getPatientService()
				.getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
		return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
	}

	private InsuranceClaimItem createTestClaimItem() {
		Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
		InsuranceClaim claim = createTestClaim();
		return InsuranceClaimItemMother.createTestInstance(concept, claim);
	}

	private InsuranceClaimDiagnosis createTestClaimDiagnosis() {
		Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
		InsuranceClaim claim = createTestClaim();
		return InsuranceClaimDiagnosisMother.createTestInstance(concept, claim);
	}
}
