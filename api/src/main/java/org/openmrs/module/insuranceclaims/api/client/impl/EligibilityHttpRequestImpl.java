package org.openmrs.module.insuranceclaims.api.client.impl;

import org.hl7.fhir.dstu3.model.EligibilityRequest;
import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.openmrs.module.insuranceclaims.api.client.EligibilityHttpRequest;

import java.net.URISyntaxException;

public class EligibilityHttpRequestImpl implements EligibilityHttpRequest {

    private FhirRequestClient client;

    @Override
    public EligibilityResponse sendEligibilityRequest(String resourceUrl, EligibilityRequest request)
            throws URISyntaxException {
        String url = resourceUrl + "/";
        EligibilityResponse response = client.postObject(url, request, EligibilityResponse.class);

        return response;
    }

    public void setClient(FhirRequestClient client) {
        this.client = client;
    }
}
