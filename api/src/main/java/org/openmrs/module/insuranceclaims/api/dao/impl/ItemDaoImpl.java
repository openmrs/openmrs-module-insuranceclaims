package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.ItemDao;
import org.openmrs.module.insuranceclaims.api.model.Item;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.ItemDao")
public class ItemDaoImpl extends HibernateOpenmrsDataDAO<Item> implements ItemDao {

	public ItemDaoImpl() {
		super(Item.class);
	}
}
