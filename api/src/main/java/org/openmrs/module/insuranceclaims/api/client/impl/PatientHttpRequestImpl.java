package org.openmrs.module.insuranceclaims.api.client.impl;

import org.hl7.fhir.dstu3.model.Patient;
import org.openmrs.module.insuranceclaims.api.client.PatientHttpRequest;

import java.net.URISyntaxException;


public class PatientHttpRequestImpl implements PatientHttpRequest {

    private FhirRequestClient client;

    @Override
    public Patient getPatientByQuery(String resourceUrl, String query) throws URISyntaxException {
        String url = resourceUrl + "/" + query;
        return  client.getObject(url, Patient.class);

    }

    public void setClient(FhirRequestClient fhirRequestClient) {
        this.client = fhirRequestClient;
    }
}
