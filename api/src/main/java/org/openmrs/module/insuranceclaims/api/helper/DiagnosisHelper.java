package org.openmrs.module.insuranceclaims.api.helper;

import org.openmrs.Concept;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;


public interface DiagnosisHelper {

    void setConcept(InsuranceClaimDiagnosis diagnosis, Concept concept);

    void setInsuranceClaim(InsuranceClaimDiagnosis diagnosis, InsuranceClaim claim);

}
