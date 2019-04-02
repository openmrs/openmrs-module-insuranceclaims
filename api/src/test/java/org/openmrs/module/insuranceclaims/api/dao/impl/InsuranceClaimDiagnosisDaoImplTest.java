package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimDiagnosisMother;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class InsuranceClaimDiagnosisDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private InsuranceClaimDiagnosisDao dao;

	@Test
	public void saveInsuranceClaimDiagnosis_shouldSaveAllPropertiesInDb() {
		InsuranceClaimDiagnosis diagnosis = InsuranceClaimDiagnosisMother.createTestInstance();

		dao.saveInsuranceClaimDiagnosis(diagnosis);

		Context.flushSession();
		Context.clearSession();

		InsuranceClaimDiagnosis savedDiagnosis = dao.getInsuranceClaimDiagnosisByUuid(diagnosis.getUuid());

		Assert.assertThat(savedDiagnosis, hasProperty("uuid", is(diagnosis.getUuid())));
		Assert.assertThat(savedDiagnosis, hasProperty("concept", is(diagnosis.getConcept())));
		Assert.assertEquals(diagnosis.getInsuranceClaim(), savedDiagnosis.getInsuranceClaim());

		InsuranceClaimDiagnosis sameDiagnosis = dao.getInsuranceClaimDiagnosisById(savedDiagnosis.getId());
		Assert.assertEquals(savedDiagnosis, sameDiagnosis);
	}
}
