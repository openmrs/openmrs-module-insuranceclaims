package org.openmrs.module.insuranceclaims.api.client.impl;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.module.insuranceclaims.api.client.ClaimHttpRequest;
import org.openmrs.module.insuranceclaims.api.client.FHIRClient;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimDiagnosisService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimItemService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRInsuranceClaimService;
import org.springframework.web.client.HttpServerErrorException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class ClaimHttpRequestImpl implements ClaimHttpRequest {

    private FHIRClient fhirRequestClient;

    private FHIRInsuranceClaimService fhirInsuranceClaimService;

    private FHIRClaimDiagnosisService fhirClaimDiagnosisService;

    private FHIRClaimItemService fhirClaimItemService;


    private List<String> errors;

    public void setFhirInsuranceClaimService(FHIRInsuranceClaimService fhirInsuranceClaimService) {
        this.fhirInsuranceClaimService = fhirInsuranceClaimService;
    }

    public void setFhirClaimDiagnosisService(FHIRClaimDiagnosisService fhirClaimDiagnosisService) {
        this.fhirClaimDiagnosisService = fhirClaimDiagnosisService;
    }

    public void setFhirClaimItemService(FHIRClaimItemService fhirClaimItemService) {
        this.fhirClaimItemService = fhirClaimItemService;
    }

    public void setFhirRequestClient(FHIRClient fhirRequestClient) {
        this.fhirRequestClient = fhirRequestClient;
    }

    @Override
    public ClaimRequestWrapper getClaimRequest(String resourceUrl, String claimCode)
            throws URISyntaxException {
        String url = resourceUrl + "/" + claimCode;
        Claim receivedClaim = fhirRequestClient.getObject(url, Claim.class);
        return wrapResponse(receivedClaim);
    }

    @Override
    public ClaimResponse sendClaimRequest(String resourceUrl, InsuranceClaim insuranceClaim)
            throws URISyntaxException, HttpServerErrorException, FHIRException {
        String url = resourceUrl + "/";
        Claim claimToSend = fhirInsuranceClaimService.generateClaim(insuranceClaim);
        return fhirRequestClient.postObject(url, claimToSend, ClaimResponse.class);
    }

    @Override
    public ClaimResponse getClaimResponse(String baseUrl, String claimCode) throws URISyntaxException {
        String url = baseUrl + "/" + claimCode;
        return fhirRequestClient.getObject(url, ClaimResponse.class);
    }

    @Override
    public List<String> getErrors() {
        return errors != null ? errors : Collections.emptyList();
    }

    private ClaimRequestWrapper wrapResponse(Claim claim) {
        this.errors = new ArrayList<>();

        InsuranceClaim receivedClaim = fhirInsuranceClaimService.generateOmrsClaim(claim, errors);
        List<InsuranceClaimDiagnosis> receivedDiagnosis = claim.getDiagnosis().stream()
                .map(diagnosis -> fhirClaimDiagnosisService.createOmrsClaimDiagnosis(diagnosis, errors))
                .collect(Collectors.toList());
        List<InsuranceClaimItem> items = fhirClaimItemService.generateOmrsClaimItems(claim, errors);

        return new ClaimRequestWrapper(receivedClaim, receivedDiagnosis, items);
    }
}