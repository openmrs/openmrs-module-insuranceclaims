package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.insuranceclaims.api.dao.ItemCodeDao;
import org.openmrs.module.insuranceclaims.api.model.ItemCode;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.ItemCodeDao")
public class ItemCodeDaoImpl implements ItemCodeDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	public DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public ItemCode getItemCodeById(Integer id) {
		return (ItemCode) getSession().createCriteria(ItemCode.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.ID_FIELD_NAME, id)).uniqueResult();
	}

	@Override
	public ItemCode getItemCodeByUuid(String uuid) {
		return (ItemCode) getSession().createCriteria(ItemCode.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.UUID_FIELD_NAME, uuid)).uniqueResult();
	}

	@Override
	public ItemCode saveItemCode(ItemCode itemCode) {
		getSession().saveOrUpdate(itemCode);
		return itemCode;
	}
}
