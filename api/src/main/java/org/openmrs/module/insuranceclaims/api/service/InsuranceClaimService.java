package org.openmrs.module.insuranceclaims.api.service;

import org.openmrs.api.APIException;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import java.util.List;

public interface InsuranceClaimService extends OpenmrsDataService<InsuranceClaim> {
    List<InsuranceClaim> getAllInsuranceClaims(Integer patientId) throws APIException;
}
