package org.openmrs.module.insuranceclaims.api.strategies.consumed;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.ProvidedItemService;
import org.openmrs.module.insuranceclaims.api.service.exceptions.ConsumedItemException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Set;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONCEPT_PRICE_ATTRIBUTE_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMABLES_LIST_ITEMS_CONCEPT_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMED_ITEMS_CONCEPT_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.QUANTITY_CONSUMED_CONCEPT_UUID;

@Component("DefaultConsumedItemStrategy")
@Transactional
public class ConsumedItemStrategy implements GenericConsumedItemStrategy {

    private ProvidedItemService providedItemService;

    @Override
    public void addProvidedItems(Obs newObs) throws ConsumedItemException {
        if (newObs.getConcept().getUuid().equals(CONSUMED_ITEMS_CONCEPT_UUID)) {
            Set<Obs> obsMembers = newObs.getGroupMembers();
            if (obsMembers != null) {
                int unitsConsumed = getNumberOfConsumptions(obsMembers);
                Obs consumedItem = getConsumedItemConcept(obsMembers);
                createProvidedItems(consumedItem, unitsConsumed);
            }
        }
    }

    private int getNumberOfConsumptions(Set<Obs> obsMembers) {
        return obsMembers.stream()
                .filter(obs -> obs.getConcept().getUuid().equals(QUANTITY_CONSUMED_CONCEPT_UUID))
                .findFirst()
                .map(obs -> obs.getValueNumeric())
                .orElse(1.0)
                .intValue();
    }

    private Obs getConsumedItemConcept(Set<Obs> obsMembers) throws ConsumedItemException {
        return obsMembers.stream()
                .filter(obs -> obs.getConcept().getUuid().equals(CONSUMABLES_LIST_ITEMS_CONCEPT_UUID))
                .findFirst()
                .orElseThrow(() -> new ConsumedItemException("Could not create provided item, no concept found"));
    }

    private void createProvidedItems(Obs consumedItem, int numberOfConsumptions) throws ConsumedItemException {
        BigDecimal itemPrice = getConceptPrice(consumedItem.getValueCoded());
        createProvidedItemForObservation(consumedItem, itemPrice, numberOfConsumptions);
    }

    private void createProvidedItemForObservation(Obs consumedItem, BigDecimal price, int numberOfConsumptions) {
        ProvidedItem newProvidedItem = new ProvidedItem();

        Concept consumedItemConcept = consumedItem.getValueCoded();
        newProvidedItem.setItem(consumedItemConcept);

        int patientId = consumedItem.getPerson().getPersonId();
        Patient patient = Context.getPatientService().getPatient(patientId);
        newProvidedItem.setPatient(patient);

        newProvidedItem.setDateOfServed(consumedItem.getObsDatetime());
        newProvidedItem.setStatus(ProcessStatus.ENTERED);
        newProvidedItem.setNumberOfConsumptions(numberOfConsumptions);
        newProvidedItem.setPrice(price);

        providedItemService.saveOrUpdate(newProvidedItem);
    }

    private static BigDecimal getConceptPrice(Concept concept) throws ConsumedItemException {

        Float value =  (Float) concept
                .getAttributes()
                .stream()
                .filter(c -> c.getAttributeType().getUuid().equals(CONCEPT_PRICE_ATTRIBUTE_UUID))
                .map(c -> c.getValue())
                .findFirst()
                .orElseThrow(() -> new ConsumedItemException("Could not find price for requested concept"));

        return new BigDecimal(value.toString());
    }

    public void setProvidedItemService(ProvidedItemService providedItemService) {
        this.providedItemService = providedItemService;
    }
}
