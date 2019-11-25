package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.module.insuranceclaims.api.dao.ProvidedItemDao;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.ProvidedItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("insuranceclaims.ProvidedItemService")
@Transactional
public class ProvidedItemServiceImpl extends BaseOpenmrsDataService<ProvidedItem> implements ProvidedItemService {

    @Autowired
    private ProvidedItemDao providedItemDAO;

    @Override
    public List<ProvidedItem> getProvidedItems(Integer patientId, ProcessStatus processStatus) {
        return providedItemDAO.getProvidedItems(patientId, processStatus);
    }

    @Override
    public List<ProvidedItem> getProvidedEnteredItems(Integer patientId) {
        return providedItemDAO.getProvidedItems(patientId, ProcessStatus.ENTERED);
    }

    @Override
    public void updateStatusProvidedItems(List<ProvidedItem> providedItems) {
        for (ProvidedItem item : providedItems) {
            item.setStatus(ProcessStatus.PROCESSED);
            this.saveOrUpdate(item);
        }
    }
}
