package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;

public interface InsuranceClaimItemDao {

	InsuranceClaimItem getInsuranceClaimItemById(Integer id);

	InsuranceClaimItem getInsuranceClaimItemByUuid(String uuid);

	InsuranceClaimItem saveInsuranceClaimItem(InsuranceClaimItem claimItem);
}
