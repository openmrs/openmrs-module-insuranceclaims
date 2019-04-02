package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.module.insuranceclaims.api.model.Item;

public final class ItemMother {

	/**
	 * Creates the Item's test instance
	 *
	 * @return - the Item instance
	 */
	public static Item createTestInstance() {
		Item item = new Item();
		item.setName("some name");
		item.setDescription("some description");
		return item;
	}

	private ItemMother() {
	}
}
