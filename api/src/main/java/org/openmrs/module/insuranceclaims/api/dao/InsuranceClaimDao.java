package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

public interface InsuranceClaimDao {

	InsuranceClaim getInsuranceClaimById(Integer id);

	InsuranceClaim getInsuranceClaimByUuid(String uuid);

	InsuranceClaim saveInsuranceClaim(InsuranceClaim claim);
}
