package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.insuranceclaims.api.dao.ItemConceptDao;
import org.openmrs.module.insuranceclaims.api.model.ItemConcept;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.ItemConceptDao")
public class ItemConceptDaoImpl implements ItemConceptDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	public DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public ItemConcept getItemConceptById(Integer id) {
		return (ItemConcept) getSession().createCriteria(ItemConcept.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.ID_FIELD_NAME, id)).uniqueResult();
	}

	@Override
	public ItemConcept getItemConceptByUuid(String uuid) {
		return (ItemConcept) getSession().createCriteria(ItemConcept.class)
				.add(Restrictions.eq(InsuranceClaimsConstants.UUID_FIELD_NAME, uuid)).uniqueResult();
	}

	@Override
	public ItemConcept saveItemConcept(ItemConcept itemConcept) {
		getSession().saveOrUpdate(itemConcept);
		return itemConcept;
	}
}
