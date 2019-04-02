package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

public final class InsuranceClaimDiagnosisMother {

	private static final int TEST_CONCEPT_ID = 3;

	/**
	 * Creates the InsuranceClaimDiagnosis's test instance
	 *
	 * @return - the InsuranceClaimDiagnosis instance
	 */
	public static InsuranceClaimDiagnosis createTestInstance() {
		InsuranceClaimDiagnosis diagnosis = new InsuranceClaimDiagnosis();
		diagnosis.setConcept(Context.getConceptService().getConcept(TEST_CONCEPT_ID));
		diagnosis.setInsuranceClaim(InsuranceClaimMother.createTestInstance());
		return diagnosis;
	}

	private InsuranceClaimDiagnosisMother() {
	}
}
