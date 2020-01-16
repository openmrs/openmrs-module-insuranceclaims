package org.openmrs.module.insuranceclaims.api.service;

import javassist.NotFoundException;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;

import java.util.List;

public interface InsuranceClaimItemService extends OpenmrsDataService<InsuranceClaimItem> {

    InsuranceClaimItem updateInsuranceClaimItem(InsuranceClaimItem itemToUpdate, InsuranceClaimItem itemUpdated);

    List<InsuranceClaimItem> updateInsuranceClaimItems(List<InsuranceClaimItem> itemsToUpdate,
                                                       List<InsuranceClaimItem> itemsUpdated) throws NotFoundException;
}
