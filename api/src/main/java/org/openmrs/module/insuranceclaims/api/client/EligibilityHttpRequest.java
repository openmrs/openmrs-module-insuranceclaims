package org.openmrs.module.insuranceclaims.api.client;

import org.hl7.fhir.dstu3.model.EligibilityRequest;
import org.hl7.fhir.dstu3.model.EligibilityResponse;

import java.net.URISyntaxException;

public interface EligibilityHttpRequest {

    EligibilityResponse sendEligibilityRequest(String resourceUrl, EligibilityRequest request) throws URISyntaxException;
}
