package org.openmrs.module.insuranceclaims;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.EventListener;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.insuranceclaims.api.service.exceptions.ConsumedItemException;
import org.openmrs.module.insuranceclaims.api.strategies.consumed.ConsumedItemStrategyUtil;
import org.openmrs.module.insuranceclaims.api.strategies.consumed.GenericConsumedItemStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

public class ItemConsumedEventListener implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceClaimsActivator.class);

    private DaemonToken daemonToken;

    private GenericConsumedItemStrategy consumedItemStrategy;

    public ItemConsumedEventListener(DaemonToken daemonToken) {
        this.daemonToken = daemonToken;
        this.consumedItemStrategy = ConsumedItemStrategyUtil.getObservationStrategy();
    }

    @Override
    public void onMessage(Message message) {
        Daemon.runInDaemonThread(new Runnable() {
            @Override
            public void run() {
                    processMessage(message);
            }
        }, daemonToken);
    }

    private void processMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;
        try {
            String obsUuid = mapMessage.getString("uuid");
            Obs newObs = Context.getObsService().getObsByUuid(obsUuid);
            consumedItemStrategy.addProvidedItems(newObs);
        } catch (JMSException e) {
            LOG.error("Insurance claim: Exception during objectifying: " + e);
        } catch (ConsumedItemException e) {
            LOG.error("Insurance claim: Error during creating provided item: " + e);
        }
    }
}
