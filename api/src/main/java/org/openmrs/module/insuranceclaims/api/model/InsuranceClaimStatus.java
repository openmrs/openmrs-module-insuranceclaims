package org.openmrs.module.insuranceclaims.api.model;

/**
 * The allowable {@link InsuranceClaim} statuses.
 */
public enum InsuranceClaimStatus {
	REJECTED, ENTERED, CHECKED, PROCESSED, VALUATED;

	public int getValue() {
		return ordinal() + 1;
	}
}
