package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.ItemPriceDao;
import org.openmrs.module.insuranceclaims.api.model.ItemPrice;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.ItemPriceDao")
public class ItemPriceDaoImpl extends HibernateOpenmrsDataDAO<ItemPrice> implements ItemPriceDao {

	public ItemPriceDaoImpl() {
		super(ItemPrice.class);
	}
}
