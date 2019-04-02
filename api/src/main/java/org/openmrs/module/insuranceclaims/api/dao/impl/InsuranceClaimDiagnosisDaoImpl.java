package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.InsuranceClaimDiagnosisDao")
public class InsuranceClaimDiagnosisDaoImpl implements InsuranceClaimDiagnosisDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	public DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public InsuranceClaimDiagnosis getInsuranceClaimDiagnosisById(Integer id) {
		return (InsuranceClaimDiagnosis) getSession().createCriteria(InsuranceClaimDiagnosis.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.ID_FIELD_NAME, id)).uniqueResult();
	}

	@Override
	public InsuranceClaimDiagnosis getInsuranceClaimDiagnosisByUuid(String uuid) {
		return (InsuranceClaimDiagnosis) getSession().createCriteria(InsuranceClaimDiagnosis.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.UUID_FIELD_NAME, uuid)).uniqueResult();
	}

	@Override
	public InsuranceClaimDiagnosis saveInsuranceClaimDiagnosis(InsuranceClaimDiagnosis diagnosis) {
		getSession().saveOrUpdate(diagnosis);
		return diagnosis;
	}
}
