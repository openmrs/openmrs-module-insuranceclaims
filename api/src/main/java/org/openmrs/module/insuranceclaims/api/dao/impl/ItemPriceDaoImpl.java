package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.insuranceclaims.api.dao.ItemPriceDao;
import org.openmrs.module.insuranceclaims.api.model.ItemPrice;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.ItemPriceDao")
public class ItemPriceDaoImpl implements ItemPriceDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public ItemPrice getItemPriceById(Integer id) {
		return (ItemPrice) getSession().createCriteria(ItemPrice.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.ID_FIELD_NAME, id)).uniqueResult();
	}

	@Override
	public ItemPrice getItemPriceByUuid(String uuid) {
		return (ItemPrice) getSession().createCriteria(ItemPrice.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.UUID_FIELD_NAME, uuid)).uniqueResult();
	}

	@Override
	public ItemPrice saveItemPrice(ItemPrice itemPrice) {
		getSession().saveOrUpdate(itemPrice);
		return itemPrice;
	}
}
