package org.openmrs.module.insuranceclaims.api.client;


import org.hl7.fhir.dstu3.model.Patient;

import java.net.URISyntaxException;
import java.util.List;

public interface PatientHttpRequest {

    Patient getPatientByQuery(String resourceUrl, String claimCode) throws URISyntaxException;

    List<Patient> getPatientByIdentifier(String resourceUrl, String query) throws URISyntaxException;
}
