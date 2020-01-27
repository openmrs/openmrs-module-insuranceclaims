package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import java.util.List;

public interface InsuranceClaimDao extends BaseOpenmrsCriteriaDao<InsuranceClaim> {
    List<InsuranceClaim> getAllInsuranceClaims(Integer patientId);
}
