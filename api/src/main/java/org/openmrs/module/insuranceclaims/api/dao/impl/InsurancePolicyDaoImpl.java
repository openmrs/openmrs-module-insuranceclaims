package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.InsurancePolicyDao;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;

public class InsurancePolicyDaoImpl extends HibernateOpenmrsDataDAO<InsurancePolicy> implements InsurancePolicyDao {

	public InsurancePolicyDaoImpl() {
		super(InsurancePolicy.class);
	}
}
