package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.dao.ProvidedItemDao;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;

public class ProvidedItemDaoImpl extends HibernateOpenmrsDataDAO<ProvidedItem> implements ProvidedItemDao {

    public ProvidedItemDaoImpl() {
        super(ProvidedItem.class);
    }
}
