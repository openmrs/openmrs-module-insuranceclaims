package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.module.insuranceclaims.api.model.ItemPrice;

public interface ItemPriceDao {

	ItemPrice getItemPriceById(Integer id);

	ItemPrice getItemPriceByUuid(String uuid);

	ItemPrice saveItemPrice(ItemPrice itemPrice);
}
