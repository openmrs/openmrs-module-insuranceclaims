package org.openmrs.module.insuranceclaims.api.service;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

public interface InsuranceClaimService extends OpenmrsDataService<InsuranceClaim> {

    InsuranceClaim updateClaim(InsuranceClaim claimToUpdate, InsuranceClaim updatedClaim);
}
