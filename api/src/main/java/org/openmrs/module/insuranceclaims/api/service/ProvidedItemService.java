package org.openmrs.module.insuranceclaims.api.service;

import org.openmrs.module.insuranceclaims.api.dao.ProvidedItemDao;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;

import java.util.List;

public interface ProvidedItemService extends OpenmrsDataService<ProvidedItem> {

    List<ProvidedItem> getProvidedItems(Integer patientId, ProcessStatus processStatus);
    List<ProvidedItem> getProvidedEnteredItems(Integer patientId);
    void updateStatusProvidedItems(List<ProvidedItem> providedItems);
    ProvidedItemDao getProvidedItemDao();
}
