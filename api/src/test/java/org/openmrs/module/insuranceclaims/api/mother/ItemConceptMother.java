package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Concept;

public final class ItemConceptMother {

	/**
	 * Creates the ItemConcept's test instance
	 *
	 * @param concept - related concept object
	 * @return - the ItemConcept instance
	 */
	public static ItemConcept createTestInstanceWithConcept(Concept concept) {
		ItemConcept itemConcept = new ItemConcept();
		itemConcept.setConcept(concept);
		itemConcept.setItem(ItemMother.createTestInstance());
		return itemConcept;
	}

	private ItemConceptMother() {
	}
}
