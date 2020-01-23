package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.module.insuranceclaims.api.dao.BaseOpenmrsDataDao;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

public class InsuranceClaimDaoImpl extends BaseOpenmrsDataDao<InsuranceClaim> implements InsuranceClaimDao {

	public InsuranceClaimDaoImpl() {
		super(InsuranceClaim.class);
	}
}
