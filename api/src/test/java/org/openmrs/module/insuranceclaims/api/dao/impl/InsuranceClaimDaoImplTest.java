package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class InsuranceClaimDaoImplTest extends BaseModuleContextSensitiveTest {

	private static final int TEST_LOCATION_ID = 1;

	private static final int TEST_PROVIDER_ID = 1;

	private static final int TEST_VISIT_TYPE_ID = 1;

	private static final Integer TEST_IDENTIFIER_TYPE_ID = 2;

	@Autowired
	private InsuranceClaimDao dao;

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
		Assert.assertThat(savedInsuranceClaim, hasProperty("dateFrom",
				is(new Timestamp(insuranceClaim.getDateFrom().getTime()))));
		Assert.assertThat(savedInsuranceClaim, hasProperty("dateTo",
				is(new Timestamp(insuranceClaim.getDateTo().getTime()))));
		Assert.assertThat(savedInsuranceClaim, hasProperty("adjustment", is(insuranceClaim.getAdjustment())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("claimedTotal", is(insuranceClaim.getClaimedTotal())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("approvedTotal", is(insuranceClaim.getApprovedTotal())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("dateProcessed",
				is(new Timestamp(insuranceClaim.getDateProcessed().getTime()))));
		Assert.assertThat(savedInsuranceClaim, hasProperty("explanation", is(insuranceClaim.getExplanation())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("rejectionReason", is(insuranceClaim.getRejectionReason())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("guaranteeId", is(insuranceClaim.getGuaranteeId())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("visitType", is(insuranceClaim.getVisitType())));
		Assert.assertThat(savedInsuranceClaim, hasProperty("claimStatus", is(insuranceClaim.getClaimStatus())));
	}

	private InsuranceClaim createTestInstance() {
		Location location = Context.getLocationService().getLocation(TEST_LOCATION_ID);
		Provider provider = Context.getProviderService().getProvider(TEST_PROVIDER_ID);
		VisitType visitType = Context.getVisitService().getVisitType(TEST_VISIT_TYPE_ID);
		PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(TEST_IDENTIFIER_TYPE_ID);
		return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
	}
}
