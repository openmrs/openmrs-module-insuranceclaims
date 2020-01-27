package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.module.insuranceclaims.api.dao.BaseOpenmrsDataDao;
import org.openmrs.module.insuranceclaims.api.dao.InsurancePolicyDao;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;

public class InsurancePolicyDaoImpl extends BaseOpenmrsDataDao<InsurancePolicy> implements InsurancePolicyDao {

	public InsurancePolicyDaoImpl() {
		super(InsurancePolicy.class);
	}
}
