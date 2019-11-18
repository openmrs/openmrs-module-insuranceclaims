package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.BillDao;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.BillDao")
public class BillDaoImpl extends HibernateOpenmrsDataDAO<Bill> implements BillDao {

    public BillDaoImpl() {
        super(Bill.class);
    }
}
