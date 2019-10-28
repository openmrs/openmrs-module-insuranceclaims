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
import org.openmrs.module.insuranceclaims.api.testutils.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class InsuranceClaimDiagnosisDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private InsuranceClaimDiagnosisDao dao;

	@Test
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
		Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
		Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
		Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
		VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
		PatientIdentifierType identifierType = Context.getPatientService()
				.getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
		InsuranceClaim insuranceClaim = InsuranceClaimMother.createTestInstance(location, provider, visitType,
				identifierType);
		return InsuranceClaimDiagnosisMother.createTestInstance(concept, insuranceClaim);
	}
}
