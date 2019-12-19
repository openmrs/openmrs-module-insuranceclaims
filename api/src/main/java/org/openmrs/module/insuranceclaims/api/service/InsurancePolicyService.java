package org.openmrs.module.insuranceclaims.api.service;

import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;

import java.util.Date;

public interface InsurancePolicyService extends OpenmrsDataService<InsurancePolicy> {

    InsurancePolicy generateInsurancePolicy(EligibilityResponse response);

    String getPolicyIdFromContractReference(Reference contract);

    Date getExpireDateFromContractReference(Reference contract);
}
