package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItemStatus;
import org.openmrs.module.insuranceclaims.api.model.Item;

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
	public static InsuranceClaimItem createTestInstance(InsuranceClaim insuranceClaim) {
		Item item = ItemMother.createTestInstance();
		InsuranceClaimItem claimItem = new InsuranceClaimItem();
		claimItem.setQuantityApproved(EXAMPLE_QUANTITY_APPROVED);
		claimItem.setQuantityProvided(EXAMPLE_QUANTITY_PROVIDED);
		claimItem.setPriceApproved(new BigDecimal(EXAMPLE_PRICE_APPROVED));
		claimItem.setPriceAsked(ItemPriceMother.createTestInstanceWithItem(item));
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
