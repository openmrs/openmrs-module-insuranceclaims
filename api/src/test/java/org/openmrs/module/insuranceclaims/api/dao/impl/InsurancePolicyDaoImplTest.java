package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.InsurancePolicyDao;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.mother.InsurancePolicyMother;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class InsurancePolicyDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private InsurancePolicyDao dao;

	@Test
	public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
		Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
		PatientIdentifierType identifierType = Context.getPatientService()
				.getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
		InsurancePolicy policy = InsurancePolicyMother.createTestInstance(location, identifierType);

		dao.saveOrUpdate(policy);

		Context.flushSession();
		Context.clearSession();

		InsurancePolicy savedPolicy = dao.getByUuid(policy.getUuid());

		Assert.assertThat(savedPolicy, hasProperty("uuid", is(policy.getUuid())));
		Assert.assertThat(savedPolicy, hasProperty("startDate", is(policy.getStartDate())));
		Assert.assertThat(savedPolicy, hasProperty("expiryDate", is(policy.getExpiryDate())));
		Assert.assertThat(savedPolicy, hasProperty("patient", is(policy.getPatient())));
		Assert.assertThat(savedPolicy, hasProperty("policyStatus", is(policy.getPolicyStatus())));
	}
}
