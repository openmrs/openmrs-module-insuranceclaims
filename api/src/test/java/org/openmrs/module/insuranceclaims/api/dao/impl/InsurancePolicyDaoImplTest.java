package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
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

	@Autowired
	private InsurancePolicyDao dao;

	@Test
	public void saveInsurancePolicy_shouldSaveAllPropertiesInDb() {
		InsurancePolicy policy = InsurancePolicyMother.createTestInstance();

		dao.saveInsurancePolicy(policy);

		Context.flushSession();
		Context.clearSession();

		InsurancePolicy savedPolicy = dao.getInsurancePolicyByUuid(policy.getUuid());

		Assert.assertThat(savedPolicy, hasProperty("uuid", is(policy.getUuid())));
		Assert.assertThat(savedPolicy, hasProperty("startDate", is(new Timestamp(policy.getStartDate().getTime()))));
		Assert.assertThat(savedPolicy, hasProperty("expiryDate", is(new Timestamp(policy.getExpiryDate().getTime()))));
		Assert.assertThat(savedPolicy, hasProperty("patient", is(policy.getPatient())));
		Assert.assertThat(savedPolicy, hasProperty("policyStatus", is(policy.getPolicyStatus())));

		InsurancePolicy samePolicy = dao.getInsurancePolicyById(savedPolicy.getId());
		Assert.assertEquals(savedPolicy, samePolicy);
	}
}
