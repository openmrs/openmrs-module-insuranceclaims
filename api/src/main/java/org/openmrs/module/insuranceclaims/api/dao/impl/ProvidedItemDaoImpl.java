package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.ProvidedItemDao;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("insuranceclaims.ProvidedItemDao")
public class ProvidedItemDaoImpl extends HibernateOpenmrsDataDAO<ProvidedItem> implements ProvidedItemDao {

    @Autowired
    private DbSessionFactory sessionFactory;

    public ProvidedItemDaoImpl() {
        super(ProvidedItem.class);
    }

    @Override
    public List<ProvidedItem> getProvidedItems(Integer patientId, ProcessStatus processStatus) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("status", processStatus));
        crit.createCriteria("patient")
                .add(Restrictions.eq("patientId", patientId));

        return crit.list();
    }

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }
}
