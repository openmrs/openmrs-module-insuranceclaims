package org.openmrs.module.insuranceclaims.api.helper.impl;

import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.helper.InsuranceClaimHelper;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value = "insuranceclaims.InsuranceClaimHelper")
public class InsuranceClaimHelperImpl implements InsuranceClaimHelper {

    @Autowired
    private InsuranceClaimDao insuranceClaimDao;

    @Override
    public List<InsuranceClaimDiagnosis> getInsuranceClaimDiagnosis(InsuranceClaim claim) {
        return insuranceClaimDao.findInsuranceClaimDiagnosis(claim);
    }
}
