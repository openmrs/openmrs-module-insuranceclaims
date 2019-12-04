package org.openmrs.module.insuranceclaims.api.service.db.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.service.db.ItemDbService;

import java.util.List;

public class ItemDbServiceImpl extends BaseOpenmrsService implements ItemDbService {

    private DbSessionFactory dbSessionFactory;

    /**
     * Finds all InsuranceClaimDiagnosis that are related to InsuranceClaim
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

    public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
        this.dbSessionFactory = dbSessionFactory;
    }

    private DbSession getCurrentSession() {
        return dbSessionFactory.getCurrentSession();
    }
}
