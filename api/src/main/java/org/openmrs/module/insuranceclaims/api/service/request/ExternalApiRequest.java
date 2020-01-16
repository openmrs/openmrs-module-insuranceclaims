package org.openmrs.module.insuranceclaims.api.service.request;

import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.module.insuranceclaims.api.client.impl.ClaimRequestWrapper;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.exceptions.ClaimRequestException;

import java.net.URISyntaxException;

public interface ExternalApiRequest {
    ClaimRequestWrapper getClaimFromExternalApi(String claimCode) throws URISyntaxException;

    ClaimRequestWrapper getClaimResponseFromExternalApi(String claimCode) throws URISyntaxException, FHIRException;

    ClaimResponse sendClaimToExternalApi(InsuranceClaim claim) throws ClaimRequestException;

    InsuranceClaim updateClaim(InsuranceClaim claim) throws ClaimRequestException;
}
