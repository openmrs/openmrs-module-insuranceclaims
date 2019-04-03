package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Concept;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

public final class InsuranceClaimDiagnosisMother {

	/**
	 * Creates the InsuranceClaimDiagnosis's test instance
	 *
	 * @param concept - related Concept object
	 * @param insuranceClaim - related insurance claim object
	 * @return - the InsuranceClaimDiagnosis instance
	 */
	public static InsuranceClaimDiagnosis createTestInstance(Concept concept, InsuranceClaim insuranceClaim) {
		InsuranceClaimDiagnosis diagnosis = new InsuranceClaimDiagnosis();
		diagnosis.setConcept(concept);
		diagnosis.setInsuranceClaim(insuranceClaim);
		return diagnosis;
	}

	private InsuranceClaimDiagnosisMother() {
	}
}
