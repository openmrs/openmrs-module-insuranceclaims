package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

import java.util.List;

public interface InsuranceClaimDiagnosisDao extends OpenmrsDataDAO<InsuranceClaimDiagnosis> {

    List<InsuranceClaimDiagnosis> findInsuranceClaimDiagnosis(Integer insuranceClaimId);
}
