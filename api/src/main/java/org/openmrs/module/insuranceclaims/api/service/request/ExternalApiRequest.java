package org.openmrs.module.insuranceclaims.api.service.request;

import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Patient;
import org.openmrs.module.insuranceclaims.api.client.impl.ClaimRequestWrapper;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.service.exceptions.ClaimRequestException;
import org.openmrs.module.insuranceclaims.api.service.exceptions.EligibilityRequestException;
import org.openmrs.module.insuranceclaims.api.service.exceptions.PatientRequestException;

import java.net.URISyntaxException;
import java.util.List;

public interface ExternalApiRequest {
    ClaimRequestWrapper getClaimFromExternalApi(String claimCode) throws URISyntaxException;

    ClaimRequestWrapper getClaimResponseFromExternalApi(String claimCode) throws URISyntaxException, FHIRException;

    ClaimResponse sendClaimToExternalApi(InsuranceClaim claim) throws ClaimRequestException;

    InsuranceClaim updateClaim(InsuranceClaim claim) throws ClaimRequestException;

    InsurancePolicy getPatientPolicy(String policyNumber) throws EligibilityRequestException;

    Patient getPatient(String patientID) throws PatientRequestException;

    List<org.hl7.fhir.dstu3.model.Patient> getPatientsByIdentifier(String patientIdentifier) throws PatientRequestException;

    Patient importPatient(String patientID) throws PatientRequestException;
}
