package org.openmrs.module.insuranceclaims.api.service.db;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;

import java.util.List;

public interface ItemDbService {

    List<InsuranceClaimItem> findInsuranceClaimItems(int insuranceClaimId);
}
