package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
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
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class InsuranceClaimDiagnosisDaoImplTest extends BaseModuleContextSensitiveTest {

	private static final int TEST_CONCEPT_ID = 3;

	private static final int TEST_LOCATION_ID = 1;

	private static final int TEST_PROVIDER_ID = 1;

	private static final int TEST_VISIT_TYPE_ID = 1;

	private static final Integer TEST_IDENTIFIER_TYPE_ID = 2;

	@Autowired
	private InsuranceClaimDiagnosisDao dao;

	@Test
	@Transactional
	public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
		InsuranceClaimDiagnosis diagnosis = createTestInstance();

		dao.saveOrUpdate(diagnosis);

		Context.flushSession();
		Context.clearSession();

		InsuranceClaimDiagnosis savedDiagnosis = dao.getByUuid(diagnosis.getUuid());

		Assert.assertThat(savedDiagnosis, hasProperty("uuid", is(diagnosis.getUuid())));
		Assert.assertThat(savedDiagnosis, hasProperty("concept", is(diagnosis.getConcept())));
		Assert.assertThat(savedDiagnosis, hasProperty("insuranceClaim", is(diagnosis.getInsuranceClaim())));

	}

	private InsuranceClaimDiagnosis createTestInstance() {
		Concept concept = Context.getConceptService().getConcept(TEST_CONCEPT_ID);
		Location location = Context.getLocationService().getLocation(TEST_LOCATION_ID);
		Provider provider = Context.getProviderService().getProvider(TEST_PROVIDER_ID);
		VisitType visitType = Context.getVisitService().getVisitType(TEST_VISIT_TYPE_ID);
		PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(TEST_IDENTIFIER_TYPE_ID);
		InsuranceClaim insuranceClaim = InsuranceClaimMother.createTestInstance(location, provider, visitType,
				identifierType);
		return InsuranceClaimDiagnosisMother.createTestInstance(concept, insuranceClaim);
	}
}
