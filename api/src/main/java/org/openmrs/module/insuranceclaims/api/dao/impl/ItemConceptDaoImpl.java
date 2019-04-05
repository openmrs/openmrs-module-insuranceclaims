package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.ItemConceptDao;
import org.openmrs.module.insuranceclaims.api.model.ItemConcept;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.ItemConceptDao")
public class ItemConceptDaoImpl extends HibernateOpenmrsDataDAO<ItemConcept> implements ItemConceptDao {

	public ItemConceptDaoImpl() {
		super(ItemConcept.class);
	}
}
