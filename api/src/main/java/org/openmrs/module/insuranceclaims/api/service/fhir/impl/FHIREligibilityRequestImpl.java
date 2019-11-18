package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import org.hl7.fhir.dstu3.model.EligibilityRequest;
import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIREligibilityService;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil;

import java.util.List;

public class FHIREligibilityRequestImpl implements FHIREligibilityService {

    @Override
    public EligibilityRequest generateEligibilityRequest(InsuranceClaim claim) {
        EligibilityRequest request = new EligibilityRequest();

        //Identifier
        List<Identifier> identifier = InsuranceClaimUtil.createClaimIdentifier(claim);
        request.setIdentifier(identifier);

        //Patient (Should use chfid for reference)
        Reference patient = new Reference().setReference("Patient/" + claim.getPatient().getUuid());
        request.setPatient(patient);

        //TODO: Category and subcategory will be added after database update

        return request;
    }

    @Override
    public InsurancePolicy generateEligibilityResponse(EligibilityResponse patient) {
        return null;
    }
}
