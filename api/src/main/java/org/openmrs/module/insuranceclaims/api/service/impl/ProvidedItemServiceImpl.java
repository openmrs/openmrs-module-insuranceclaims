package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.ProvidedItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("insuranceclaims.ProvidedItemService")
@Transactional
public class ProvidedItemServiceImpl extends BaseOpenmrsDataService<ProvidedItem> implements ProvidedItemService {
}
