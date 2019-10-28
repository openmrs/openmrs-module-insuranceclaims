package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.VisitType;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("insuranceclaims.InsuranceClaimDao")
public class InsuranceClaimDaoImpl extends HibernateOpenmrsDataDAO<InsuranceClaim> implements InsuranceClaimDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	public InsuranceClaimDaoImpl() {
		super(InsuranceClaim.class);
	}


	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * Finds all VisitTypes with name given name
	 *
	 * @param visitTypeName - Name of visit type
	 */
	public List<VisitType> findVisitTypeByName(String  visitTypeName) {
		Criteria crit =  getSession().createCriteria(VisitType.class);
		crit.add(Restrictions.eq("name", visitTypeName));
		List<VisitType> result = crit.list();
		return result;
	}

	/**
	 * Finds all InsuranceClaimDiagnosis that are related to InsuranceClaim
	 *
	 * @param ic - InsuranceClaim object
	 */
	@Override
	public List<InsuranceClaimDiagnosis> findInsuranceClaimDiagnosis(InsuranceClaim ic) {
		Criteria crit = getSession().createCriteria(InsuranceClaimDiagnosis.class, "d");
		crit.createAlias("d.insuranceClaim", "claim");

		crit.add(Restrictions.eq("claim.id", ic.getId()));
		List<InsuranceClaimDiagnosis> result = crit.list();

		return result;
	}
}
