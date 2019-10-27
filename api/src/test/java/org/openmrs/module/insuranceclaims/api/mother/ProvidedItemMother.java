package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;

import java.math.BigDecimal;
import java.util.Date;

public class ProvidedItemMother {

    public static ProvidedItem createTestInstance() {
        ProvidedItem item = new ProvidedItem();
        item.setPrice(new BigDecimal(123.45));
        item.setDateOfServed(new Date());

        return item;
    }

    public ProvidedItemMother() {
    }
}
