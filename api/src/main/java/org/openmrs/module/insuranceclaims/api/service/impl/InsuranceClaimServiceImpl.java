package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;

import java.util.List;

public class InsuranceClaimServiceImpl extends BaseOpenmrsDataService<InsuranceClaim> implements InsuranceClaimService {
    private InsuranceClaimDao insuranceClaimDao;

    public void setInsuranceClaimDao(InsuranceClaimDao insuranceClaimDao) {
        this.insuranceClaimDao = insuranceClaimDao;
    }

    @Override
    public List<InsuranceClaim> getAllInsuranceClaims(Integer patientId) throws APIException {
        return this.insuranceClaimDao.getAllInsuranceClaims(patientId);
    }
}
