package org.openmrs.module.insuranceclaims.api.service.fhir;

import org.hl7.fhir.dstu3.model.Claim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

import java.util.List;

public interface FHIRClaimDiagnosisService {

    Claim.DiagnosisComponent createClaimDiagnosisComponent(InsuranceClaimDiagnosis omrsClaimDiagnosis);

    List<Claim.DiagnosisComponent> createClaimDiagnosisComponent(
     List<InsuranceClaimDiagnosis> omrsClaimDiagnosis);

    List<Claim.DiagnosisComponent> createClaimDiagnosisComponent(InsuranceClaim omrsInsuranceClaim);

    InsuranceClaimDiagnosis createOmrsClaimDiagnosis(
            Claim.DiagnosisComponent claimDiagnosis, List<String> errors);

}
