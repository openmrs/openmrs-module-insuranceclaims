package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

public class InsuranceClaimDaoImpl extends HibernateOpenmrsDataDAO<InsuranceClaim> implements InsuranceClaimDao {

	public InsuranceClaimDaoImpl() {
		super(InsuranceClaim.class);
	}
}
