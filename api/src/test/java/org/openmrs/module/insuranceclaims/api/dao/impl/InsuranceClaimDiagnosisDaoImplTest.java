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
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimDiagnosisMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class InsuranceClaimDiagnosisDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private InsuranceClaimDiagnosisDao dao;

	private InsuranceClaim insuranceClaim;

	@Before
	public void setUp() {
		this.insuranceClaim = createTestInstance();
	}

	@Test
	public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
		InsuranceClaimDiagnosis diagnosis = createTestDiagnosis();

		dao.saveOrUpdate(diagnosis);

		Context.flushSession();
		Context.clearSession();

		InsuranceClaimDiagnosis savedDiagnosis = dao.getByUuid(diagnosis.getUuid());

		Assert.assertThat(savedDiagnosis, hasProperty("uuid", is(diagnosis.getUuid())));
		Assert.assertThat(savedDiagnosis, hasProperty("concept", is(diagnosis.getConcept())));
		Assert.assertThat(savedDiagnosis, hasProperty("claim", is(diagnosis.getClaim())));

	}

	@Test
	public void findInsuranceDiagnosis_shouldReturnAllDiagnosisRelatedToClaim() {
		InsuranceClaimDiagnosis diagnosis = createTestDiagnosis();

		dao.saveOrUpdate(diagnosis);

		Context.flushSession();
		Context.clearSession();

		InsuranceClaim insuranceClaim = diagnosis.getInsuranceClaim();
		InsuranceClaimDiagnosis savedDiagnosis = dao.getByUuid(diagnosis.getUuid());

		List<InsuranceClaimDiagnosis> received = dao.findInsuranceClaimDiagnosis(insuranceClaim.getId());
		Assert.assertThat(received, hasItem(savedDiagnosis));
	}

	private InsuranceClaimDiagnosis createTestDiagnosis() {
		Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
		return InsuranceClaimDiagnosisMother.createTestInstance(concept, this.insuranceClaim);
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
