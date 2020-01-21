package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.APIException;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.BillDao;
import org.openmrs.module.insuranceclaims.api.model.Bill;

import java.util.List;

public class BillDaoImpl extends HibernateOpenmrsDataDAO<Bill> implements BillDao {
    private DbSessionFactory sessionFactory;

    public BillDaoImpl() {
        super(Bill.class);
    }

    @Override
    public List<Bill> getAllBills(Integer patientId) throws APIException {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.createCriteria("patient")
                .add(Restrictions.eq("patientId", patientId));

        return crit.list();
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }
}
