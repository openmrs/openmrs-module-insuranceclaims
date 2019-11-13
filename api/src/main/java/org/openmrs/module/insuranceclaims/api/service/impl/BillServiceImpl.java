package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.service.BillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("insuranceclaims.BillService")
@Transactional
public class BillServiceImpl extends BaseOpenmrsDataService<Bill> implements BillService {
}
