package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import java.util.List;

public interface InsuranceClaimDao extends OpenmrsDataDAO<InsuranceClaim> {
    List<InsuranceClaim> getAllInsuranceClaims(Integer patientId);
}
