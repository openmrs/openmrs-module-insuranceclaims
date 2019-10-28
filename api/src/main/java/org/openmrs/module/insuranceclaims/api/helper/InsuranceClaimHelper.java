package org.openmrs.module.insuranceclaims.api.helper;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

import java.util.List;

public interface InsuranceClaimHelper {

    List<InsuranceClaimDiagnosis> getInsuranceClaimDiagnosis(InsuranceClaim claim);


}
