package org.openmrs.module.insuranceclaims.api.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;

import java.util.List;

public abstract class BaseOpenmrsDataDao<T extends BaseOpenmrsData> extends HibernateOpenmrsDataDAO<T>
        implements BaseOpenmrsCriteriaDao<T> {

    public BaseOpenmrsDataDao(Class<T> mappedClass) {
        super(mappedClass);
    }

    @Override
    public List<T> findAllByCriteria(Criteria criteria, boolean includeVoided) {
        if (!includeVoided) {
            criteria.add(Restrictions.eq("voided", false));
        }
        return criteria.list();
    }

    @Override
    public T findByCriteria(Criteria criteria, boolean includeVoided) {
        if (!includeVoided) {
            criteria.add(Restrictions.eq("voided", false));
        }
        return (T) criteria.uniqueResult();
    }

    @Override
    public Criteria createCriteria() {
        return getSession().createCriteria(this.mappedClass);
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
