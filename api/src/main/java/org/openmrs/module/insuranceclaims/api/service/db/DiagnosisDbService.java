package org.openmrs.module.insuranceclaims.api.service.db;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

import java.util.List;

public interface DiagnosisDbService {

    List<InsuranceClaimDiagnosis> findInsuranceClaimDiagnosis(int insuranceClaimId);
}
