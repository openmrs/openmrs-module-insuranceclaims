package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.module.insuranceclaims.api.dao.BaseOpenmrsDataDao;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;

public class InsuranceClaimItemDaoImpl extends BaseOpenmrsDataDao<InsuranceClaimItem> implements InsuranceClaimItemDao {

	public InsuranceClaimItemDaoImpl() {
		super(InsuranceClaimItem.class);
	}
}
