package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.InsuranceClaimItemDao")
public class InsuranceClaimItemDaoImpl extends HibernateOpenmrsDataDAO<InsuranceClaimItem> implements InsuranceClaimItemDao {

	public InsuranceClaimItemDaoImpl() {
		super(InsuranceClaimItem.class);
	}
}
