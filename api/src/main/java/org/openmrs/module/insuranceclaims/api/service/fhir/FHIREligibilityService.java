package org.openmrs.module.insuranceclaims.api.service.fhir;

import org.hl7.fhir.dstu3.model.EligibilityRequest;

public interface FHIREligibilityService {
    EligibilityRequest generateEligibilityRequest(String policyId);
}