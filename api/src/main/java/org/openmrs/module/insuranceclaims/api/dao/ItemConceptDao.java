package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.module.insuranceclaims.api.model.ItemConcept;

public interface ItemConceptDao {

	ItemConcept getItemConceptById(Integer id);

	ItemConcept getItemConceptByUuid(String uuid);

	ItemConcept saveItemConcept(ItemConcept itemConcept);
}
