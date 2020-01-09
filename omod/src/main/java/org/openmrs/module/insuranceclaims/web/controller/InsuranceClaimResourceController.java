package org.openmrs.module.insuranceclaims.web.controller;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.NewClaimForm;
import org.openmrs.module.insuranceclaims.api.service.forms.ClaimFormService;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
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
    ClaimFormService claimFormService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Object create(@RequestBody NewClaimForm form,
                         HttpServletRequest request, HttpServletResponse response) throws ResponseException {
        InsuranceClaim claim = claimFormService.createClaim(form);
        return "{\"created claim\": \"" + claim.toString() + "\"}";
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Object get(@RequestParam String resource, HttpServletRequest request,
                      HttpServletResponse response) throws ResponseException {
        return "{\"claim\": \"" + resource + "\"}";
    }
}
