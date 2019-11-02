package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItemStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;

import java.math.BigDecimal;
import java.util.UUID;

public final class InsuranceClaimItemMother {

	private static final int EXAMPLE_QUANTITY_PROVIDED = 5;

	private static final int EXAMPLE_QUANTITY_APPROVED = 2;

	private static final String EXAMPLE_PRICE_APPROVED = "1234567890.12";

	/**
	 * Creates the InsuranceClaimItem's test instance
	 *
	 * @param insuranceClaim - related insurance claim object
	 * @return - the InsuranceClaimItem instance
	 */
	public static InsuranceClaimItem createTestInstance(Concept concept, InsuranceClaim insuranceClaim) {
		Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
		PatientIdentifierType identifierType = Context.getPatientService()
				.getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
		ProvidedItem item = ProvidedItemMother.createTestInstance(concept,location, identifierType);

		InsuranceClaimItem claimItem = new InsuranceClaimItem();
		claimItem.setQuantityApproved(EXAMPLE_QUANTITY_APPROVED);
		claimItem.setQuantityProvided(EXAMPLE_QUANTITY_PROVIDED);
		claimItem.setPriceApproved(new BigDecimal(EXAMPLE_PRICE_APPROVED));
		claimItem.setExplanation(UUID.randomUUID().toString());
		claimItem.setJustification(UUID.randomUUID().toString());
		claimItem.setRejectionReason(UUID.randomUUID().toString());
		claimItem.setItem(item);
		claimItem.setInsuranceClaim(insuranceClaim);
		claimItem.setClaimItemStatus(InsuranceClaimItemStatus.PASSED);
		return claimItem;
	}

	private InsuranceClaimItemMother() {
	}
}
