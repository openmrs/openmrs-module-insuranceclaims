package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.service.InsurancePolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("insuranceclaims.InsurancePolicyService")
@Transactional
public class InsurancePolicyServiceImpl extends BaseOpenmrsDataService<InsurancePolicy> implements InsurancePolicyService {
}
