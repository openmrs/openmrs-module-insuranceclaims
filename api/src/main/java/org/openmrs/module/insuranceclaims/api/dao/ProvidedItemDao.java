package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;

import java.util.List;

public interface ProvidedItemDao extends OpenmrsDataDAO<ProvidedItem> {

    List<ProvidedItem> getProvidedItems(Integer patientId, ProcessStatus processStatus);
}
