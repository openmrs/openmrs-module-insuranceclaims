package org.openmrs.module.insuranceclaims.api.service.fhir;

import org.hl7.fhir.dstu3.model.EligibilityRequest;
import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;

public interface FHIREligibilityService {
    EligibilityRequest generateEligibilityRequest(InsuranceClaim claim);

    InsurancePolicy generateEligibilityResponse(EligibilityResponse patient);
}
