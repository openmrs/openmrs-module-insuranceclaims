package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.springframework.stereotype.Repository;

@Repository("insuranceclaims.ProvidedItem")
public class ProvidedItemDaoImpl extends HibernateOpenmrsDataDAO<ProvidedItem> {

    public ProvidedItemDaoImpl() {
        super(ProvidedItem.class);
    }
}
