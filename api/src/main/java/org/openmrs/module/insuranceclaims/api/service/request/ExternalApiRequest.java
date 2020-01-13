package org.openmrs.module.insuranceclaims.api.service.request;

import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.module.insuranceclaims.api.client.impl.ClaimRequestWrapper;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import java.net.URISyntaxException;
import java.util.List;

public interface ExternalApiRequest {
    ClaimRequestWrapper getClaimFromExternalApi(String claimCode) throws URISyntaxException;

    ClaimRequestWrapper getClaimResponseFromExternalApi(String claimCode) throws URISyntaxException, FHIRException;

    ClaimResponse sendClaimToExternalApi(InsuranceClaim claim) throws URISyntaxException, FHIRException;

    List<String> getErrors();
}
