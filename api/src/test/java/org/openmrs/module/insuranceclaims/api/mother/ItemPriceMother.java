package org.openmrs.module.insuranceclaims.api.mother;

import java.math.BigDecimal;

public final class ItemPriceMother {

	/**
	 * Creates the ItemPrice's test instance
	 *
	 * @return - the ItemPrice instance
	 */
	public static ItemPrice createTestInstance() {
		return createTestInstanceWithItem(ItemMother.createTestInstance());
	}

	/**
	 * Creates the ItemPrice's test instance with the specific related item object
	 *
	 * @param item - the related item
	 * @return - the ItemPrice instance
	 */
	public static ItemPrice createTestInstanceWithItem(Item item) {
		ItemPrice itemPrice = new ItemPrice();
		itemPrice.setName("someName");
		itemPrice.setPrice(new BigDecimal("1234567890.12"));
		itemPrice.setItem(item);
		return itemPrice;
	}

	private ItemPriceMother() {
	}
}
