package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;

import java.util.List;

public interface InsuranceClaimItemDao extends OpenmrsDataDAO<InsuranceClaimItem> {

    List<InsuranceClaimItem> findInsuranceClaimItems(int insuranceClaimId);
}
