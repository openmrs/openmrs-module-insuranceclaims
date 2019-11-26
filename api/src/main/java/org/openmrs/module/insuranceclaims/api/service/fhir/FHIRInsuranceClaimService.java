package org.openmrs.module.insuranceclaims.api.service.fhir;

import org.hl7.fhir.dstu3.model.Claim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import java.util.List;

public interface FHIRInsuranceClaimService {
    Claim generateClaim(InsuranceClaim omrsClaim);

    InsuranceClaim generateOmrsClaim(Claim claim, List<String> errors);
}
