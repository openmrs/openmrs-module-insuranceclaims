package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicyStatus;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;

import java.util.Date;

public final class InsurancePolicyMother {

	/**
	 * Creates the InsurancePolicy's test instance
	 *
	 * @param location - related location object
	 * @param identifierType - related identifier type object
	 * @return - the InsurancePolicy instance
	 */
	public static InsurancePolicy createTestInstance(Location location, PatientIdentifierType identifierType) {
		InsurancePolicy policy = new InsurancePolicy();
		policy.setStartDate(new Date());
		policy.setExpiryDate(new Date());
		policy.setPatient(PatientMother.createTestInstance(location, identifierType));
		policy.setStatus(InsurancePolicyStatus.ACTIVE);
		policy.setPolicyNumber(TestConstants.TEST_PATIENT_POLICY_NUMBER);
		return policy;
	}

	private InsurancePolicyMother() {
	}
}
