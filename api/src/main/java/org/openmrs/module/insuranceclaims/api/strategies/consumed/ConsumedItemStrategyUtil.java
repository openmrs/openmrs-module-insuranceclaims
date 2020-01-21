package org.openmrs.module.insuranceclaims.api.strategies.consumed;

import org.openmrs.api.context.Context;

public class ConsumedItemStrategyUtil {

    public static GenericConsumedItemStrategy getObservationStrategy() {
        String strategy = Context.getAdministrationService().getGlobalProperty("insuranceclaims.consumeditem.strategy");

        return strategy == null ? new ConsumedItemStrategy() :
                Context.getRegisteredComponent(strategy, GenericConsumedItemStrategy.class);
    }
}
