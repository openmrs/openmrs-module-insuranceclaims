package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;

import java.util.List;

public interface ProvidedItemDao extends BaseOpenmrsCriteriaDao<ProvidedItem> {

    List<ProvidedItem> getProvidedItems(Integer patientId, ProcessStatus processStatus);
}
