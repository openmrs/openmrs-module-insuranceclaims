package org.openmrs.module.insuranceclaims.api.dao;

import org.hibernate.Criteria;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.api.db.OpenmrsDataDAO;

import java.util.List;

public interface BaseOpenmrsCriteriaDao<T extends BaseOpenmrsData> extends OpenmrsDataDAO<T> {

    /**
     * Return a list of persistents (optionally voided) which are compatible with provided criteria
     * It can be called by users with this module's privilege. It is executed in a transaction.
     *
     * @param criteria - specific criteria
     * @param includeVoided - if true voided persistents are also returned
     * @return a list of persistents of the given class
     */
    List<T> findAllByCriteria(Criteria criteria, boolean includeVoided);

    /**
     * Return a single persistent (optionally voided) which is compatible with provided criteria.
     * Expects that will exist unique result.
     * It can be called by users with this module's privilege. It is executed in a transaction.
     *
     * @param criteria - specific criteria
     * @param includeVoided - if true voided persistents are also returned
     * @return a list of persistents of the given class
     */
    T findByCriteria(Criteria criteria, boolean includeVoided);

    /**
     * Return a criteria created for specific class which extends {@link BaseOpenmrsData}
     *
     * @return a new criteria
     */
    Criteria createCriteria();
}
