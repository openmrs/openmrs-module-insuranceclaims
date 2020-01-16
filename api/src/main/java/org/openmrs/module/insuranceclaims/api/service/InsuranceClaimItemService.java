package org.openmrs.module.insuranceclaims.api.service;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.service.exceptions.ItemMatchingFailedException;

import java.util.List;

public interface InsuranceClaimItemService extends OpenmrsDataService<InsuranceClaimItem> {

    InsuranceClaimItem updateInsuranceClaimItem(InsuranceClaimItem itemToUpdate, InsuranceClaimItem itemUpdated);

    List<InsuranceClaimItem> updateInsuranceClaimItems(List<InsuranceClaimItem> itemsToUpdate,
                                                       List<InsuranceClaimItem> itemsUpdated) throws ItemMatchingFailedException;
}
