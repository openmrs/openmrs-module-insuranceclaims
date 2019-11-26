package org.openmrs.module.insuranceclaims.api.service.fhir;

import org.hl7.fhir.dstu3.model.EligibilityRequest;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

public interface FHIREligibilityService {
    EligibilityRequest generateEligibilityRequest(InsuranceClaim claim);
}
