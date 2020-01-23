package org.openmrs.module.insuranceclaims.api.strategies.consumed;

import org.openmrs.api.context.Context;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMED_ITEM_STRATEGY_PROPERTY;

public class ConsumedItemStrategyUtil {

    public static GenericConsumedItemStrategy getObservationStrategy() {
        String strategy = Context.getAdministrationService().getGlobalProperty(CONSUMED_ITEM_STRATEGY_PROPERTY);

        return strategy == null ? new ConsumedItemStrategy() :
                Context.getRegisteredComponent(strategy, GenericConsumedItemStrategy.class);
    }
}
