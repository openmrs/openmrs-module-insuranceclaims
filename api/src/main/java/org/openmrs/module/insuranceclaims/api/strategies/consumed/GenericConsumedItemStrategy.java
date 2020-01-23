package org.openmrs.module.insuranceclaims.api.strategies.consumed;

import org.openmrs.Obs;
import org.openmrs.module.insuranceclaims.api.service.exceptions.ConsumedItemException;

public interface GenericConsumedItemStrategy {

    void addProvidedItems(Obs observation) throws ConsumedItemException;
}
