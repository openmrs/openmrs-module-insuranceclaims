package org.openmrs.module.insuranceclaims.api.service.request.impl;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.client.ClaimHttpRequest;
import org.openmrs.module.insuranceclaims.api.client.impl.ClaimRequestWrapper;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimItemService;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.service.db.ItemDbService;
import org.openmrs.module.insuranceclaims.api.service.exceptions.ClaimRequestException;
import org.openmrs.module.insuranceclaims.api.service.exceptions.ItemMatchingFailedException;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimDiagnosisService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimItemService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimResponseService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRInsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil;
import org.openmrs.module.insuranceclaims.api.service.request.ExternalApiRequest;
import org.springframework.web.client.HttpServerErrorException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.insuranceclaims.api.client.ClientConstants.BASE_URL_PROPERTY;
import static org.openmrs.module.insuranceclaims.api.client.ClientConstants.CLAIM_RESPONSE_SOURCE_URI;
import static org.openmrs.module.insuranceclaims.api.client.ClientConstants.CLAIM_SOURCE_URI;

public class ExternalApiRequestImpl implements ExternalApiRequest {

    private String claimResponseUrl;
    private String claimUrl;

    private ClaimHttpRequest claimHttpRequest;

    private FHIRInsuranceClaimService fhirInsuranceClaimService;

    private FHIRClaimItemService fhirClaimItemService;

    private FHIRClaimResponseService fhirClaimResponseService;

    private FHIRClaimDiagnosisService fhirClaimDiagnosisService;

    private InsuranceClaimService insuranceClaimService;

    private InsuranceClaimItemService insuranceClaimItemService;

    private ItemDbService itemDbService;

    @Override
    public ClaimRequestWrapper getClaimFromExternalApi(String claimCode) throws URISyntaxException {
        setUrls();
        Claim claim = claimHttpRequest.getClaimRequest(this.claimUrl, claimCode);
        return wrapResponse(claim);
    }

    @Override
    public ClaimRequestWrapper getClaimResponseFromExternalApi(String claimCode) throws URISyntaxException, FHIRException {
        setUrls();
        ClaimResponse response = claimHttpRequest.getClaimResponse(this.claimResponseUrl, claimCode);
        return wrapResponse(response);
    }

    @Override
    public ClaimResponse sendClaimToExternalApi(InsuranceClaim claim) throws ClaimRequestException {
        try {
            setUrls();
            ClaimResponse claimResponse = claimHttpRequest.sendClaimRequest(claimUrl, claim);
            String externalCode = InsuranceClaimUtil.getClaimResponseId(claimResponse);
            claim.setExternalId(externalCode);
            insuranceClaimService.saveOrUpdate(claim);
            return claimResponse;
        } catch (URISyntaxException | FHIRException requestException) {
            String exceptionMessage = "Exception occured during processing request: "
                    + "Message:" + requestException.getMessage();

            throw new ClaimRequestException(exceptionMessage, requestException);
        } catch (HttpServerErrorException e) {
            String exceptionMessage = "Exception occured during processing request: "
                    + "Message:" + e.getMessage()
                    + "Reason: " + e.getResponseBodyAsString();

            throw new ClaimRequestException(exceptionMessage, e);
        }
    }

    @Override
    public InsuranceClaim updateClaim(InsuranceClaim claim) throws ClaimRequestException {
        try {
            setUrls();
            String claimExternalId = claim.getExternalId();
            //For now it is assumed that external server don't have information that allows to match items
            // in ClaimResponse with MRS concepts, but order of items in ClaimResponse is the same as in Claim
            ClaimRequestWrapper wrappedResponse = getClaimResponseWithAssignedItemCodes(claimExternalId);
            insuranceClaimService.updateClaim(claim, wrappedResponse.getInsuranceClaim());
            List<InsuranceClaimItem> omrsItems = itemDbService.findInsuranceClaimItems(claim.getId());
            insuranceClaimItemService.updateInsuranceClaimItems(omrsItems, wrappedResponse.getItems());

            return claim;
        } catch (URISyntaxException | FHIRException | ItemMatchingFailedException requestException) {
            String exceptionMessage = "Exception occured during processing request: "
                    + "Message:" + requestException.getMessage();
            throw new ClaimRequestException(exceptionMessage, requestException);
        } catch (HttpServerErrorException e) {
            String exceptionMessage = "Exception occured during processing request: "
                    + "Message:" + e.getMessage()
                    + "Reason: " + e.getResponseBodyAsString();

            throw new ClaimRequestException(exceptionMessage, e);
        }
    }

    public void setClaimHttpRequest(ClaimHttpRequest claimHttpRequest) {
        this.claimHttpRequest = claimHttpRequest;
    }

    public void setFhirInsuranceClaimService(FHIRInsuranceClaimService fhirInsuranceClaimService) {
        this.fhirInsuranceClaimService = fhirInsuranceClaimService;
    }

    public void setFhirClaimItemService(FHIRClaimItemService fhirClaimItemService) {
        this.fhirClaimItemService = fhirClaimItemService;
    }

    public void setFhirClaimResponseService(FHIRClaimResponseService fhirClaimResponseService) {
        this.fhirClaimResponseService = fhirClaimResponseService;
    }

    public void setFhirClaimDiagnosisService(FHIRClaimDiagnosisService fhirClaimDiagnosisService) {
        this.fhirClaimDiagnosisService = fhirClaimDiagnosisService;
    }

    public void setInsuranceClaimService(InsuranceClaimService insuranceClaimService) {
        this.insuranceClaimService = insuranceClaimService;
    }

    public void setInsuranceClaimItemService(InsuranceClaimItemService insuranceClaimItemService) {
        this.insuranceClaimItemService = insuranceClaimItemService;
    }

    public void setItemDbService(ItemDbService itemDbService) {
        this.itemDbService = itemDbService;
    }

    private void setUrls() {
        String baseUrl = Context.getAdministrationService().getGlobalProperty(BASE_URL_PROPERTY);
        String claimUri =  Context.getAdministrationService().getGlobalProperty(CLAIM_SOURCE_URI);
        String claimResponseUri =  Context.getAdministrationService().getGlobalProperty(CLAIM_RESPONSE_SOURCE_URI);

        this.claimResponseUrl = baseUrl + "/" + claimResponseUri;
        this.claimUrl = baseUrl + "/" + claimUri;
    }

    private ClaimRequestWrapper wrapResponse(Claim claim) {
        List<String> errors = new ArrayList<>();
        InsuranceClaim receivedClaim = fhirInsuranceClaimService.generateOmrsClaim(claim, errors);
        List<InsuranceClaimDiagnosis> receivedDiagnosis = claim.getDiagnosis().stream()
                .map(diagnosis -> fhirClaimDiagnosisService.createOmrsClaimDiagnosis(diagnosis, errors))
                .collect(Collectors.toList());
        List<InsuranceClaimItem> items = fhirClaimItemService.generateOmrsClaimItems(claim, errors);

        return new ClaimRequestWrapper(receivedClaim, receivedDiagnosis, items, errors);
    }

    private ClaimRequestWrapper wrapResponse(ClaimResponse claim) throws FHIRException {
        List<String> errors = new ArrayList<>();
        InsuranceClaim receivedClaim = fhirClaimResponseService.generateOmrsClaim(claim, errors);
        List<InsuranceClaimItem> items = fhirClaimItemService.generateOmrsClaimResponseItems(claim, errors);

        return new ClaimRequestWrapper(receivedClaim, null, items, errors);
    }

    private ClaimRequestWrapper getClaimResponseWithAssignedItemCodes(String claimCode) throws URISyntaxException, FHIRException {
        Claim claim = claimHttpRequest.getClaimRequest(this.claimUrl, claimCode);
        ClaimResponse claimResponse = claimHttpRequest.getClaimResponse(this.claimResponseUrl, claimCode);

        ClaimRequestWrapper wrappedResponse = wrapResponse(claimResponse);

        List<InsuranceClaimItem> claimResponseItems = wrappedResponse.getItems();
        List<InsuranceClaimItem> claimItems = wrapResponse(claim).getItems();

        for (int itemIndex = 0; itemIndex < claimResponseItems.size(); itemIndex++) {
            InsuranceClaimItem nextItem = claimResponseItems.get(itemIndex);
            InsuranceClaimItem itemAdditionalData = claimItems.get(itemIndex);
            addDataFromClaimItemToClaimResponseItem(nextItem, itemAdditionalData);
        }
        return wrappedResponse;
    }

    private void addDataFromClaimItemToClaimResponseItem(InsuranceClaimItem claimResponseItem, InsuranceClaimItem claimItem) {
        claimResponseItem.setItem(claimItem.getItem());
        claimResponseItem.setQuantityProvided(claimItem.getQuantityProvided());
    }
}
