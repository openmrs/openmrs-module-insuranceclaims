package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.InsurancePolicyDao;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.mother.InsurancePolicyMother;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class InsurancePolicyDaoImplTest extends BaseModuleContextSensitiveTest {

	private static final Integer FIRST_LOCATION = 1;

	private static final int TEST_IDENTIFIER_TYPE_ID = 2;

	@Autowired
	private InsurancePolicyDao dao;

	@Test
	public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
		Location location = Context.getLocationService().getLocation(FIRST_LOCATION);
		PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(TEST_IDENTIFIER_TYPE_ID);
		InsurancePolicy policy = InsurancePolicyMother.createTestInstance(location, identifierType);

		dao.saveOrUpdate(policy);

		Context.flushSession();
		Context.clearSession();

		InsurancePolicy savedPolicy = dao.getByUuid(policy.getUuid());

		Assert.assertThat(savedPolicy, hasProperty("uuid", is(policy.getUuid())));
		Assert.assertThat(savedPolicy, hasProperty("startDate", is(new Timestamp(policy.getStartDate().getTime()))));
		Assert.assertThat(savedPolicy, hasProperty("expiryDate", is(new Timestamp(policy.getExpiryDate().getTime()))));
		Assert.assertThat(savedPolicy, hasProperty("patient", is(policy.getPatient())));
		Assert.assertThat(savedPolicy, hasProperty("policyStatus", is(policy.getPolicyStatus())));
	}
}
