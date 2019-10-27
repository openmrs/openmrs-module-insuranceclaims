package org.openmrs.module.insuranceclaims.api.mother;

import java.util.UUID;

public final class ItemCodeMother {

	/**
	 * Creates the ItemCode's test instance
	 *
	 * @return - the ItemCode instance
	 */
	public static ItemCode createTestInstance() {
		ItemCode itemCode = new ItemCode();
		itemCode.setCode(UUID.randomUUID().toString());
		itemCode.setItem(ItemMother.createTestInstance());
		return itemCode;
	}

	private ItemCodeMother() {
	}
}
