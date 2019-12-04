package org.openmrs.module.insuranceclaims.api.service.db.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.service.db.DiagnosisDbService;

import java.util.List;

public class DiagnosisDbServiceImpl extends BaseOpenmrsService implements DiagnosisDbService {

    private DbSessionFactory dbSessionFactory;

    /**
     * Finds all InsuranceClaimDiagnosis that are related to InsuranceClaim
     *
     * @param insuranceClaimId - InsuranceClaim id
     */
    @Override
    public List<InsuranceClaimDiagnosis> findInsuranceClaimDiagnosis(int insuranceClaimId) {
        Criteria crit = getCurrentSession().createCriteria(InsuranceClaimDiagnosis.class, "diagnosis");
        crit.createAlias("diagnosis.claim", "claim");

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
