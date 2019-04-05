package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("insuranceclaims.InsuranceClaimService")
@Transactional
public class InsuranceClaimServiceImpl extends BaseOpenmrsDataService<InsuranceClaim> implements InsuranceClaimService {

}
