package org.openmrs.module.insuranceclaims.web.controller;

import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.module.insuranceclaims.api.client.impl.ClaimRequestWrapper;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil;
import org.openmrs.module.insuranceclaims.api.service.request.ExternalApiRequest;
import org.openmrs.module.insuranceclaims.forms.ClaimFormService;
import org.openmrs.module.insuranceclaims.forms.NewClaimForm;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;

import static org.openmrs.module.insuranceclaims.InsuranceClaimsOmodConstants.CLAIM_ALREADY_SENT_MESSAGE;

@RestController
@RequestMapping(value = "insuranceclaims/rest/v1/claims")
public class InsuranceClaimResourceController {

    /**
     * Logger for this class and subclasses
     */
    private static final Logger LOG = LoggerFactory.getLogger(InsuranceClaimsController.class);

    @Autowired
    private ClaimFormService claimFormService;

    @Autowired
    private InsuranceClaimService insuranceClaimService;

    @Autowired
    private ExternalApiRequest externalApiRequest;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<InsuranceClaim> create(@RequestBody NewClaimForm form,
                                               HttpServletRequest request, HttpServletResponse response) throws ResponseException {
        InsuranceClaim claim = claimFormService.createClaim(form);

        ResponseEntity<InsuranceClaim> requestResponse = new ResponseEntity<>(claim, HttpStatus.ACCEPTED);
        return requestResponse;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity get(@RequestParam(value = "claimId") String claimUuid,
                              HttpServletRequest request, HttpServletResponse response) throws ResponseException {
        InsuranceClaim claim = insuranceClaimService.getByUuid(claimUuid);
        ResponseEntity<InsuranceClaim> requestResponse = new ResponseEntity<>(claim, HttpStatus.ACCEPTED);
        return requestResponse;
    }

    /**
     * This method will check if claim is present in external id, if external id don't have information about this
     * claim it will send it to external system, if claim was already submitted it will get update object based on external
     * information.
     */
    @RequestMapping(value = "/sendToExternal", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity sendClaimToExternalId(
            @RequestParam(value = "claimId", required = true) String claimUuid,
            HttpServletRequest request,
            HttpServletResponse response) {
        InsuranceClaim claim = insuranceClaimService.getByUuid(claimUuid);

        if (claim.getExternalId() != null) {
            return ResponseEntity.badRequest().body(CLAIM_ALREADY_SENT_MESSAGE);
        }
        ResponseEntity responseEntity;
        try {
            ClaimResponse claimResponse = externalApiRequest.sendClaimToExternalApi(claim);
            String externalCode = InsuranceClaimUtil.getClaimResponseId(claimResponse);
            claim.setExternalId(externalCode);
            insuranceClaimService.saveOrUpdate(claim);

            if (!externalApiRequest.getErrors().isEmpty()) {
                LOG.info("Insurance claim: Errors during processing: " + externalApiRequest.getErrors().toString());
            }
            responseEntity = new ResponseEntity<>(claim, HttpStatus.EXPECTATION_FAILED);

        } catch (URISyntaxException | FHIRException requestException) {
            String exceptionMessage = "Exception occured during processing request: "
                    + "Message:" + requestException.getMessage();
            responseEntity = new ResponseEntity<>(exceptionMessage, HttpStatus.EXPECTATION_FAILED);
        } catch (HttpServerErrorException e) {
            String exceptionMessage = "Exception occured during processing request: "
                    + "Message:" + e.getMessage()
                    + "Reason: " + e.getResponseBodyAsString();
            responseEntity = new ResponseEntity<>(exceptionMessage, HttpStatus.EXPECTATION_FAILED);
        }
        return responseEntity;
    }

    /**
     * This method will try to fetch claim from external system.
     */
    @RequestMapping(value = "/getFromExternal", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity getClaimFromExternalId(@RequestParam(value = "claimId") String claimExternalCode,
                                                 HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity requestResponse;
        try {
             ClaimRequestWrapper wrapper = externalApiRequest.getClaimFromExternalApi(claimExternalCode);
             requestResponse = new ResponseEntity<>(wrapper, HttpStatus.ACCEPTED);
             request.setAttribute("errors", externalApiRequest.getErrors());
        } catch (URISyntaxException wrongUrl) {
             requestResponse = new ResponseEntity<>(wrongUrl.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }

        return requestResponse;
    }
}
