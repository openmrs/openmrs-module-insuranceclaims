package org.openmrs.module.insuranceclaims.api.helper.impl;

import org.openmrs.Concept;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.helper.DiagnosisHelper;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "insuranceclaims.DiagnosisHelper")
public class DiagnosisHelperImpl implements DiagnosisHelper {

    @Autowired
    InsuranceClaimDiagnosisDao dao;

    @Override
    public void setConcept(InsuranceClaimDiagnosis diagnosis, Concept concept) {
        diagnosis.setConcept(concept);
    }

    @Override
    public void setInsuranceClaim(InsuranceClaimDiagnosis diagnosis, InsuranceClaim claim) {
        diagnosis.setInsuranceClaim(claim);
    }

}
