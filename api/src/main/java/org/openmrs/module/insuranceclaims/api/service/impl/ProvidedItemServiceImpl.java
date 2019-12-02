package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.module.insuranceclaims.api.dao.ProvidedItemDao;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.ProvidedItemService;

import java.util.List;

public class ProvidedItemServiceImpl extends BaseOpenmrsDataService<ProvidedItem> implements ProvidedItemService {

    private ProvidedItemDao providedItemDao;

    public void setProvidedItemDao(ProvidedItemDao providedItemDao) {
        this.providedItemDao = providedItemDao;
    }

    public ProvidedItemDao getProvidedItemDao() {
        return providedItemDao;
    }

    @Override
    public List<ProvidedItem> getProvidedItems(Integer patientId, ProcessStatus processStatus) {
        return providedItemDao.getProvidedItems(patientId, processStatus);
    }

    @Override
    public List<ProvidedItem> getProvidedEnteredItems(Integer patientId) {
        return providedItemDao.getProvidedItems(patientId, ProcessStatus.ENTERED);
    }

    @Override
    public void updateStatusProvidedItems(List<ProvidedItem> providedItems) {
        for (ProvidedItem item : providedItems) {
            item.setStatus(ProcessStatus.PROCESSED);
            providedItemDao.saveOrUpdate(item);
        }
    }
}
