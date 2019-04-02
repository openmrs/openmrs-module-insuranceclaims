package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.insuranceclaims.api.dao.InsurancePolicyDao;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.InsurancePolicyDao")
public class InsurancePolicyDaoImpl implements InsurancePolicyDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	public DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public InsurancePolicy getInsurancePolicyById(Integer id) {
		return (InsurancePolicy) getSession().createCriteria(InsurancePolicy.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.ID_FIELD_NAME, id)).uniqueResult();
	}

	@Override
	public InsurancePolicy getInsurancePolicyByUuid(String uuid) {
		return (InsurancePolicy) getSession().createCriteria(InsurancePolicy.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.UUID_FIELD_NAME, uuid)).uniqueResult();
	}

	@Override
	public InsurancePolicy saveInsurancePolicy(InsurancePolicy policy) {
		getSession().saveOrUpdate(policy);
		return policy;
	}
}
