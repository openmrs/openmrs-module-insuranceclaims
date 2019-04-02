package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.InsuranceClaimDao")
public class InsuranceClaimDaoImpl implements InsuranceClaimDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	public DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public InsuranceClaim getInsuranceClaimById(Integer id) {
		return (InsuranceClaim) getSession().createCriteria(InsuranceClaim.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.ID_FIELD_NAME, id)).uniqueResult();
	}

	@Override
	public InsuranceClaim getInsuranceClaimByUuid(String uuid) {
		return (InsuranceClaim) getSession().createCriteria(InsuranceClaim.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.UUID_FIELD_NAME, uuid)).uniqueResult();
	}

	@Override
	public InsuranceClaim saveInsuranceClaim(InsuranceClaim claim) {
		getSession().saveOrUpdate(claim);
		return claim;
	}
}
