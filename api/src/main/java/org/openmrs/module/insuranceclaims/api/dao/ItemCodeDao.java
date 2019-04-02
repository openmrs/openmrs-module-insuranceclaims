package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.module.insuranceclaims.api.model.ItemCode;

public interface ItemCodeDao {

	ItemCode getItemCodeById(Integer id);

	ItemCode getItemCodeByUuid(String uuid);

	ItemCode saveItemCode(ItemCode itemCode);
}
