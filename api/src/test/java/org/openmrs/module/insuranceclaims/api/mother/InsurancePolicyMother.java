package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
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
		return createTestInstance(PatientMother.createTestInstance(location, identifierType));
	}

	/**
	 * Creates the InsurancePolicy's test instance for specific person
	 *
	 * @param person - related person object
	 * @return - the InsurancePolicy instance
	 */
	public static InsurancePolicy createTestInstance(Person person) {
		InsurancePolicy policy = new InsurancePolicy();
		policy.setStartDate(new Date());
		policy.setExpiryDate(TestConstants.TEST_PATIENT_POLICY_EXPIRY_DATE);
		policy.setPatient((Patient) person);
		policy.setStatus(InsurancePolicyStatus.ACTIVE);
		policy.setAllowedMoney(TestConstants.TEST_PATIENT_POLICY_ALLOWED_MONEY);
		policy.setPolicyNumber(TestConstants.TEST_PATIENT_POLICY_NUMBER);
		return policy;
	}

	private InsurancePolicyMother() {
	}
}
