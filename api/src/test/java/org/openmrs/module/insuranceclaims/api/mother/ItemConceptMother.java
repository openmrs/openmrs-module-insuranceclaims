package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.ItemConcept;

public final class ItemConceptMother {

	private static final int TEST_CONCEPT_ID = 3;

	/**
	 * Creates the ItemConcept's test instance
	 *
	 * @return - the ItemConcept instance
	 */
	public static ItemConcept createTestInstance() {
		ItemConcept itemConcept = new ItemConcept();
		itemConcept.setConcept(Context.getConceptService().getConcept(TEST_CONCEPT_ID));
		itemConcept.setItem(ItemMother.createTestInstance());
		return itemConcept;
	}

	private ItemConceptMother() {
	}
}
