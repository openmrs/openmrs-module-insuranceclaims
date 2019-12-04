package org.openmrs.module.insuranceclaims.api.service.fhir;

import org.hl7.fhir.dstu3.model.EligibilityRequest;
import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;

import java.util.Date;

public interface FHIREligibilityService {
    EligibilityRequest generateEligibilityRequest(String policyId);

    InsurancePolicy generateInsurancePolicy(EligibilityResponse response);

    String getPolicyIdFromContractReference(Reference contract);

    Date getExpireDateFromContractReference(Reference contract);
}