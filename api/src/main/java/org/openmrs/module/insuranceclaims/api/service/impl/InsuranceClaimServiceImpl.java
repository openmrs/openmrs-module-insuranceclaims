package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("insuranceclaims.InsuranceClaimService")
@Transactional
public class InsuranceClaimServiceImpl extends BaseOpenmrsService implements InsuranceClaimService {

	private InsuranceClaimDao dao;

	public InsuranceClaimServiceImpl() {
	}

	public InsuranceClaimServiceImpl(InsuranceClaimDao dao) {
		this.dao = dao;
	}

	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(InsuranceClaimDao dao) {
		this.dao = dao;
	}

	@Override
	@Transactional(readOnly = true)
	public InsuranceClaim getInsuranceClaimById(Integer id) throws APIException {
		return dao.getInsuranceClaimById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public InsuranceClaim getInsuranceClaimByUuid(String uuid) throws APIException {
		return dao.getInsuranceClaimByUuid(uuid);
	}

	@Override
	public InsuranceClaim saveInsuranceClaim(InsuranceClaim claim) throws APIException {
		return dao.saveInsuranceClaim(claim);
	}
}
