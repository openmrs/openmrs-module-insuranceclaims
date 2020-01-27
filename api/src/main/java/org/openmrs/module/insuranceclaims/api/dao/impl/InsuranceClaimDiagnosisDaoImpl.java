package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.module.insuranceclaims.api.dao.BaseOpenmrsDataDao;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

public class InsuranceClaimDiagnosisDaoImpl extends BaseOpenmrsDataDao<InsuranceClaimDiagnosis>
		implements InsuranceClaimDiagnosisDao {

	public InsuranceClaimDiagnosisDaoImpl() {
		super(InsuranceClaimDiagnosis.class);
	}
}
