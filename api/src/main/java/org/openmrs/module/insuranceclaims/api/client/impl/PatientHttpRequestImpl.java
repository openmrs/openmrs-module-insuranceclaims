package org.openmrs.module.insuranceclaims.api.client.impl;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.client.PatientHttpRequest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PatientHttpRequestImpl implements PatientHttpRequest {

    private FhirRequestClient client;

    @Override
    public Patient getPatientByQuery(String resourceUrl, String query) throws URISyntaxException {
        String url = resourceUrl + "/" + query;
        return  client.getObject(url, Patient.class);

    }

    @Override
    public List<Patient> getPatientByIdentifier(String resourceUrl, String identifier) throws URISyntaxException {
        String url = resourceUrl + "/?" + Context.getAdministrationService().getGlobalProperty("insuranceclaims.patientRequestId")
                + "=" + identifier;
        Bundle bundle  = client.getObject(url, Bundle.class);
        List<String> patientUrls =  bundle.getEntry().stream().map(entry -> entry.getFullUrl()).collect(Collectors.toList());
        List<Patient> patientsForId = new ArrayList<>();

        for (String patientUrl: patientUrls) {
            patientsForId.add(client.getObject(patientUrl, Patient.class));
        }
        return  patientsForId;
    }
    public void setClient(FhirRequestClient fhirRequestClient) {
        this.client = fhirRequestClient;
    }
}
