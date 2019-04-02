package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.InsuranceClaimItemDao")
public class InsuranceClaimItemDaoImpl implements InsuranceClaimItemDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	public DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public InsuranceClaimItem getInsuranceClaimItemById(Integer id) {
		return (InsuranceClaimItem) getSession().createCriteria(InsuranceClaimItem.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.ID_FIELD_NAME, id)).uniqueResult();
	}

	@Override
	public InsuranceClaimItem getInsuranceClaimItemByUuid(String uuid) {
		return (InsuranceClaimItem) getSession().createCriteria(InsuranceClaimItem.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.UUID_FIELD_NAME, uuid)).uniqueResult();
	}

	@Override
	public InsuranceClaimItem saveInsuranceClaimItem(InsuranceClaimItem claimItem) {
		getSession().saveOrUpdate(claimItem);
		return claimItem;
	}
}
