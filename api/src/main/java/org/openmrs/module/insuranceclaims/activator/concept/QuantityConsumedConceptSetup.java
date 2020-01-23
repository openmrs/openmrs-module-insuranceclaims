package org.openmrs.module.insuranceclaims.activator.concept;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.QUANTITY_CONSUMED_CONCEPT_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.QUANTITY_CONSUMED_CONCEPT_UUID;

public class QuantityConsumedConceptSetup extends AbstractConceptSetup {

    public QuantityConsumedConceptSetup() {
        super(QUANTITY_CONSUMED_CONCEPT_UUID, QUANTITY_CONSUMED_CONCEPT_NAME,
                ConceptDatatype.NUMERIC_UUID, ConceptClass.MISC_UUID);
    }

    @Override
    public void createConceptIfNotExist() {
        if (!isContextExisting()) {
            Concept quantityConsumedItems = buildConcept();
            saveConcept(quantityConsumedItems);
        }
    }
}
