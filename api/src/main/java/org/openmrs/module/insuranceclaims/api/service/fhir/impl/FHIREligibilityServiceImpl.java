package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import org.hl7.fhir.dstu3.model.EligibilityRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIREligibilityService;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.buildReference;

public class FHIREligibilityServiceImpl implements FHIREligibilityService {

    @Override
    public EligibilityRequest generateEligibilityRequest(String policyId) {
        EligibilityRequest request = new EligibilityRequest();
        Reference patient = buildReference(FHIRConstants.PATIENT, policyId);
        request.setPatient(patient);

        return request;
    }
}