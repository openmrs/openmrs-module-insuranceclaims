package org.openmrs.module.insuranceclaims.web.controller;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;
import org.openmrs.module.insuranceclaims.forms.ClaimFormService;
import org.openmrs.module.insuranceclaims.forms.NewClaimForm;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "insuranceclaims/rest/v1/claims")
public class InsuranceClaimResourceController {

    @Autowired
    private ClaimFormService claimFormService;

    @Autowired
    private InsuranceClaimService insuranceClaimService;

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
    public ResponseEntity<InsuranceClaim> get(
            @RequestParam(value = "claimId", required = true) String claimUuid,
            HttpServletRequest request,
            HttpServletResponse response) throws ResponseException {
        InsuranceClaim claim = insuranceClaimService.getByUuid(claimUuid);
        ResponseEntity<InsuranceClaim> requestResponse = new ResponseEntity<>(claim, HttpStatus.ACCEPTED);
        return requestResponse;
    }
}
