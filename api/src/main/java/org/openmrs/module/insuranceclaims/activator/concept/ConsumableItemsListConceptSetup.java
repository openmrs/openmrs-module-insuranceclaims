package org.openmrs.module.insuranceclaims.activator.concept;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMABLES_LIST_CONCEPT_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMABLES_LIST_ITEMS_CONCEPT_UUID;

public class ConsumableItemsListConceptSetup extends AbstractConceptSetup {

    public ConsumableItemsListConceptSetup() {
        super(CONSUMABLES_LIST_ITEMS_CONCEPT_UUID, CONSUMABLES_LIST_CONCEPT_NAME,
                ConceptDatatype.CODED_UUID, ConceptClass.FINDING_UUID);
    }

    @Override
    public void createConceptIfNotExist() {
        if (!isContextExisting()) {
            Concept consumedItems = buildConcept();
            saveConcept(consumedItems);
        }
    }
}
