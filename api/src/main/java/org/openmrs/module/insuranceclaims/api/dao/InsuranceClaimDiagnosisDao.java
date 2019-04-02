package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

public interface InsuranceClaimDiagnosisDao {

	InsuranceClaimDiagnosis getInsuranceClaimDiagnosisById(Integer id);

	InsuranceClaimDiagnosis getInsuranceClaimDiagnosisByUuid(String uuid);

	InsuranceClaimDiagnosis saveInsuranceClaimDiagnosis(InsuranceClaimDiagnosis diagnosis);
}
