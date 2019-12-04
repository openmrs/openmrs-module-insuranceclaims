package org.openmrs.module.insuranceclaims.api.service.fhir;

import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import java.util.List;

public interface FHIRClaimResponseService {
    ClaimResponse generateClaimResponse(InsuranceClaim omrsClaim);

    InsuranceClaim generateOmrsClaim(ClaimResponse claim, List<String> errors);
}
