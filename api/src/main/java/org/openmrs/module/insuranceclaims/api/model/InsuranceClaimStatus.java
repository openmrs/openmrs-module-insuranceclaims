package org.openmrs.module.insuranceclaims.api.model;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ENUMERATION_FROM;

/**
 * The allowable {@link InsuranceClaim} statuses.
 */
public enum InsuranceClaimStatus {
	REJECTED, ENTERED, CHECKED, PROCESSED, VALUATED;

	/**
	 * FHIR associates status with numbers starting from, i.e.:
	 * REJECTED - 1
	 * ENTERED - 2
	 * ...
	 * This method allows to correctly assign status to number
	 */
	public int getNumericStatus() {
		return ordinal() + ENUMERATION_FROM;
	}
}
