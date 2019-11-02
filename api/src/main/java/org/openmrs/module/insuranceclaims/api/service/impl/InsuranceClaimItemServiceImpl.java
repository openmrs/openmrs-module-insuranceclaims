package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("insuranceclaims.InsuranceClaimItemService")
@Transactional
public class InsuranceClaimItemServiceImpl extends BaseOpenmrsDataService<InsuranceClaimItem>
        implements InsuranceClaimItemService {
}
