package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.module.insuranceclaims.api.model.Item;

public interface ItemDao {

	Item getItemById(Integer id);

	Item getItemByUuid(String uuid);

	Item saveItem(Item item);
}
