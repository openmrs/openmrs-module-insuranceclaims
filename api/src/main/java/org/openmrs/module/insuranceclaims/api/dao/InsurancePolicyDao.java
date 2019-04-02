package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;

public interface InsurancePolicyDao {

	InsurancePolicy getInsurancePolicyById(Integer id);

	InsurancePolicy getInsurancePolicyByUuid(String uuid);

	InsurancePolicy saveInsurancePolicy(InsurancePolicy policy);
}
