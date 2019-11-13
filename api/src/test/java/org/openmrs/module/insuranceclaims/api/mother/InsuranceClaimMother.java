package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimStatus;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public final class InsuranceClaimMother {

	/**
	 * Creates the InsuranceClaim's test instance
	 *
	 * @param location - related location object
	 * @param provider - related provider object
	 * @param visitType - related visit type
	 * @param identifierType - related identifier type (used to create patient)
	 * @return - the InsuranceClaim instance
	 */
	public static InsuranceClaim createTestInstance(Location location, Provider provider, VisitType visitType,
													PatientIdentifierType identifierType) {
		Patient patient = PatientMother.createTestInstance(location, identifierType);
		Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
		Bill bill = BillMother.createTestInstance(concept);

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
		insuranceClaim.setVisitType(visitType);
		insuranceClaim.setClaimStatus(InsuranceClaimStatus.ENTERED);
		insuranceClaim.setBill(bill);
		return insuranceClaim;
	}

	private InsuranceClaimMother() {
	}
}
