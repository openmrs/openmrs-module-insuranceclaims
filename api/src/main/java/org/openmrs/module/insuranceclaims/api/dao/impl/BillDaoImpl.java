package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.module.insuranceclaims.api.dao.BaseOpenmrsDataDao;
import org.openmrs.module.insuranceclaims.api.dao.BillDao;
import org.openmrs.module.insuranceclaims.api.model.Bill;

public class BillDaoImpl extends BaseOpenmrsDataDao<Bill> implements BillDao {

    public BillDaoImpl() {
        super(Bill.class);
    }
}
