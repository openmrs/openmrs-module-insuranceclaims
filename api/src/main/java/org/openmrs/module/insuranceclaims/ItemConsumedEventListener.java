package org.openmrs.module.insuranceclaims;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.EventListener;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.ProvidedItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.MapMessage;
import javax.jms.Message;
import java.math.BigDecimal;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONCEPT_PRICE_ATTRIBUTE_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMED_ITEMS_CONCEPT_UUID;

public class ItemConsumedEventListener implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceClaimsActivator.class);

    private DaemonToken daemonToken;

    private ProvidedItemService providedItemService;

    public ItemConsumedEventListener(DaemonToken daemonToken) {
        this.daemonToken = daemonToken;
        this.providedItemService = Context.getService(ProvidedItemService.class);
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
            if (newObs != null) {
                Concept questionConcept = newObs.getConcept();
                if (questionConcept.getUuid().equals(CONSUMED_ITEMS_CONCEPT_UUID)) {
                    createProvidedItemForObservation(newObs);
                }
            }
        } catch (Exception e) {
            LOG.error("INSURNACE CLAIM: Exception during objectifying: " + e);
        } finally {
            Context.closeSession();
        }
    }

    private void createProvidedItemForObservation(Obs consumedItem) {
        ProvidedItem newProvidedItem = new ProvidedItem();
        Concept consumedItemConcept = consumedItem.getValueCoded();
        int patientId = consumedItem.getPerson().getPersonId();
        newProvidedItem.setItem(consumedItemConcept);
        Patient patient = Context.getPatientService().getPatient(patientId);

        newProvidedItem.setPatient(patient);
        newProvidedItem.setDateOfServed(consumedItem.getObsDatetime());
        newProvidedItem.setPrice(getConceptPrice(consumedItemConcept));
        newProvidedItem.setStatus(ProcessStatus.ENTERED);

        providedItemService.saveOrUpdate(newProvidedItem);
    }

    private static BigDecimal getConceptPrice(Concept concept) {
        Float value =  (Float) concept
                .getAttributes()
                .stream()
                .filter(c -> c.getAttributeType().getUuid().equals(CONCEPT_PRICE_ATTRIBUTE_UUID))
                .map(c -> c.getValue())
                .findFirst()
                .orElse(0.0f);

        return new BigDecimal(value.toString());
    }
}
