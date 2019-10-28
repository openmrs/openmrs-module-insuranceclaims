package org.openmrs.module.insuranceclaims.api.dao.impl;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimDiagnosisMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.testutils.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class InsuranceClaimDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private InsuranceClaimDao dao;

	@Autowired
	private InsuranceClaimDiagnosisDao diagnosisDao;

	private InsuranceClaim insuranceClaim;

	@Before
	public void setUp() {
		this.insuranceClaim = createTestInstance();
	}

	@Test
	public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
		InsuranceClaim insuranceClaim = createTestInstance();

		dao.saveOrUpdate(insuranceClaim);

		Context.flushSession();
		Context.clearSession();

		InsuranceClaim savedInsuranceClaim = dao.getByUuid(insuranceClaim.getUuid());

		Assert.assertThat(savedInsuranceClaim, hasProperty("uuid", is(insuranceClaim.getUuid())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("provider", is(insuranceClaim.getProvider())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("patient", is(insuranceClaim.getPatient())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("location", is(insuranceClaim.getLocation())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("claimCode", is(insuranceClaim.getClaimCode())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("dateFrom", is(insuranceClaim.getDateFrom())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("dateTo", is(insuranceClaim.getDateTo())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("adjustment", is(insuranceClaim.getAdjustment())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("claimedTotal", is(insuranceClaim.getClaimedTotal())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("approvedTotal", is(insuranceClaim.getApprovedTotal())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("dateProcessed", is(insuranceClaim.getDateProcessed())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("explanation", is(insuranceClaim.getExplanation())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("rejectionReason", is(insuranceClaim.getRejectionReason())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("guaranteeId", is(insuranceClaim.getGuaranteeId())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("visitType", is(insuranceClaim.getVisitType())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("claimStatus", is(insuranceClaim.getClaimStatus())));
	}

	@Test
	public void getDiagnosisByInsuranceClaim() {
		InsuranceClaimDiagnosis diagnosis = createTestDiagnosis();

		diagnosisDao.saveOrUpdate(diagnosis);

		Context.flushSession();
		Context.clearSession();

		InsuranceClaim insuranceClaim = diagnosis.getInsuranceClaim();
		InsuranceClaimDiagnosis savedDiagnosis = diagnosisDao.getByUuid(diagnosis.getUuid());

		List<InsuranceClaimDiagnosis> received = dao.findInsuranceClaimDiagnosis(insuranceClaim);
		Assert.assertThat(received, hasItem(savedDiagnosis));
	}

	private InsuranceClaim createTestInstance() {
		Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
		Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
		VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
		PatientIdentifierType identifierType = Context.getPatientService()
				.getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
		return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
	}

	private InsuranceClaimDiagnosis createTestDiagnosis() {
		Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
		return InsuranceClaimDiagnosisMother.createTestInstance(concept, this.insuranceClaim);
	}
}
