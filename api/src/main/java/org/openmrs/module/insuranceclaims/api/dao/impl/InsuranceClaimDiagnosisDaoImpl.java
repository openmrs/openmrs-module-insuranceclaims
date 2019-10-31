package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("insuranceclaims.InsuranceClaimDiagnosisDao")
public class InsuranceClaimDiagnosisDaoImpl extends HibernateOpenmrsDataDAO<InsuranceClaimDiagnosis>
		implements InsuranceClaimDiagnosisDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	public InsuranceClaimDiagnosisDaoImpl() {
		super(InsuranceClaimDiagnosis.class);
	}

	/**
	 * Finds all InsuranceClaimDiagnosis that are related to InsuranceClaim
	 *
	 * @param insuranceClaimId - InsuranceClaim id
	 */
	@Override
	public List<InsuranceClaimDiagnosis> findInsuranceClaimDiagnosis(int insuranceClaimId) {
		Criteria crit = getCurrentSession().createCriteria(InsuranceClaimDiagnosis.class, "diagnosis");
		crit.createAlias("diagnosis.insuranceClaim", "claim");

		crit.add(Restrictions.eq("claim.id", insuranceClaimId));
		List<InsuranceClaimDiagnosis> result = crit.list();
		return result;
	}

	private DbSession getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
}
