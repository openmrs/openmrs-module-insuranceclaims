package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.VisitType;
import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

import java.util.List;

public interface InsuranceClaimDao extends OpenmrsDataDAO<InsuranceClaim> {

    List<VisitType> findVisitTypeByName(String visitTypeName);

    List<InsuranceClaimDiagnosis> findInsuranceClaimDiagnosis(InsuranceClaim ic);

}
