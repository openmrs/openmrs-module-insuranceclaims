package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicyStatus;

import java.util.Date;

public final class InsurancePolicyMother {

	/**
	 * Creates the InsurancePolicy's test instance
	 *
	 * @return - the InsurancePolicy instance
	 */
	public static InsurancePolicy createTestInstance() {
		InsurancePolicy policy = new InsurancePolicy();
		policy.setStartDate(new Date());
		policy.setExpiryDate(new Date());
		policy.setPatient(PatientMother.createTestInstance());
		policy.setPolicyStatus(InsurancePolicyStatus.ACTIVE);
		return policy;
	}

	private InsurancePolicyMother() {
	}
}
