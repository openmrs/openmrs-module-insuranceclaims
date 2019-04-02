package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public final class InsuranceClaimMother {

	/**
	 * Creates the InsuranceClaim's test instance
	 *
	 * @return - the InsuranceClaim instance
	 */
	public static InsuranceClaim createTestInstance() {
		Location location = Context.getLocationService().getLocation(1);
		Provider provider = Context.getProviderService().getProvider(1);
		Patient patient = PatientMother.createTestInstance(location);

		InsuranceClaim insuranceClaim = new InsuranceClaim();
		insuranceClaim.setProvider(provider);
		insuranceClaim.setPatient(patient);
		insuranceClaim.setLocation(location);
		insuranceClaim.setClaimCode(UUID.randomUUID().toString());
		insuranceClaim.setDateFrom(new Date());
		insuranceClaim.setDateTo(new Date());
		insuranceClaim.setAdjustment(UUID.randomUUID().toString());
		insuranceClaim.setClaimedTotal(new BigDecimal("1234567890.21"));
		insuranceClaim.setApprovedTotal(new BigDecimal("1234567890.21"));
		insuranceClaim.setDateProcessed(new Date());
		insuranceClaim.setExplanation(UUID.randomUUID().toString());
		insuranceClaim.setRejectionReason(UUID.randomUUID().toString());
		insuranceClaim.setGuaranteeId(UUID.randomUUID().toString());
		insuranceClaim.setVisitType(Context.getVisitService().getVisitType(1));
		insuranceClaim.setClaimStatus(InsuranceClaimStatus.ENTERED);
		return insuranceClaim;
	}

	private InsuranceClaimMother() {
	}
}
