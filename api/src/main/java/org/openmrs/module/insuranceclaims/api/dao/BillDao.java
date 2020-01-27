package org.openmrs.module.insuranceclaims.api.dao;

import org.openmrs.api.APIException;
import org.openmrs.module.insuranceclaims.api.model.Bill;

import java.util.List;

public interface BillDao extends BaseOpenmrsCriteriaDao<Bill> {
    List<Bill> getAllBills(Integer patientId) throws APIException;
}
