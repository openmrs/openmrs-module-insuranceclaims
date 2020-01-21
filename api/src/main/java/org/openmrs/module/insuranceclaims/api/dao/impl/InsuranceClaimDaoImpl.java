package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import java.util.List;

public class InsuranceClaimDaoImpl extends HibernateOpenmrsDataDAO<InsuranceClaim> implements InsuranceClaimDao {
	private DbSessionFactory sessionFactory;

	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public InsuranceClaimDaoImpl() {
		super(InsuranceClaim.class);
	}

	@Override
	public List<InsuranceClaim> getAllInsuranceClaims(Integer patientId) {
		Criteria crit = getSession().createCriteria(this.mappedClass);
		crit.createCriteria("patient")
				.add(Restrictions.eq("patientId", patientId));

		return crit.list();
	}

	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
}
