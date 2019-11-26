package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("insuranceclaims.InsuranceClaimItemDao")
public class InsuranceClaimItemDaoImpl extends HibernateOpenmrsDataDAO<InsuranceClaimItem> implements InsuranceClaimItemDao {


	private DbSessionFactory sessionFactory;

	public InsuranceClaimItemDaoImpl() {
		super(InsuranceClaimItem.class);
	}

	/**
	 * Finds all InsuranceClaimItem that are related to InsuranceClaim
	 *
	 * @param insuranceClaimId - InsuranceClaim id
	 */
	@Override
	public List<InsuranceClaimItem> findInsuranceClaimItems(int insuranceClaimId) {
		Criteria crit = getCurrentSession().createCriteria(InsuranceClaimItem.class, "item");
		crit.createAlias("item.claim", "claim");

		crit.add(Restrictions.eq("claim.id", insuranceClaimId));
		return crit.list();
	}

	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private DbSession getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
}
