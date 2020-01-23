package org.openmrs.module.insuranceclaims.activator.concept;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.context.Context;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMABLES_LIST_ITEMS_CONCEPT_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMED_ITEMS_CONCEPT_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMED_ITEMS_CONCEPT_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.QUANTITY_CONSUMED_CONCEPT_UUID;

public class ComplexConsumedItemConceptSetup extends AbstractConceptSetup {

    public ComplexConsumedItemConceptSetup() {
        super(CONSUMED_ITEMS_CONCEPT_UUID, CONSUMED_ITEMS_CONCEPT_NAME,
                ConceptDatatype.N_A_UUID, ConceptClass.CONVSET_UUID);
    }

    @Override
    protected Concept buildConcept() {
        Concept complexConcept = super.buildConcept();
        complexConcept.addSetMember(Context.getConceptService().getConceptByUuid(CONSUMABLES_LIST_ITEMS_CONCEPT_UUID));
        complexConcept.addSetMember(Context.getConceptService().getConceptByUuid(QUANTITY_CONSUMED_CONCEPT_UUID));
        return complexConcept;
    }
}
