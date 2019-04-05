package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.ItemCodeDao;
import org.openmrs.module.insuranceclaims.api.model.ItemCode;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.ItemCodeDao")
public class ItemCodeDaoImpl extends HibernateOpenmrsDataDAO<ItemCode> implements ItemCodeDao {

	public ItemCodeDaoImpl() {
		super(ItemCode.class);
	}
}
