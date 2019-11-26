package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Money;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;

import java.util.Collection;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CATEGORY_ITEM;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CATEGORY_SERVICE;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONCEPT_PRICE_ATTRIBUTE_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.IS_SERVICE_CONCEPT_ATTRIBUTE_UUID;

public final class InsuranceClaimItemUtil {
    public static SimpleQuantity getItemQuantity(InsuranceClaimItem item) {
        int quantity = item.getQuantityProvided();
        SimpleQuantity itemQuantity = new SimpleQuantity();
        itemQuantity.setValue(quantity);
        return itemQuantity;
    }

    public static Integer getItemQuantity(Claim.ItemComponent item) {
        if (item.getQuantity() != null) {
            return item.getQuantity().getValue().intValue();
        }
        else {
            return null;
        }
    }

    public static CodeableConcept getItemCategory(InsuranceClaimItem item) {
        ProvidedItem providedItem = item.getItem();
        String category = getConceptCategory(providedItem.getItem());
        return new CodeableConcept().setText(category);
    }

    public static Money getItemUnitPrice(InsuranceClaimItem item) {
        ProvidedItem providedItem = item.getItem();
        Money unitPrice = new Money();
        unitPrice.setValue(getConceptUnitPrice(providedItem.getItem()));
        return unitPrice;
    }

    public static CodeableConcept createFhirItemService(InsuranceClaimItem item) {
        ProvidedItem providedItem = item.getItem();
        String externalCode = getExternalCode(providedItem.getItem());
        Coding coding = new Coding();
        coding.setCode(externalCode);
        CodeableConcept serviceConcept = new CodeableConcept();
        serviceConcept.addCoding(coding);
        serviceConcept.setText(externalCode);
        return serviceConcept;
    }

    private static String getExternalCode(Concept concept) {
        Collection<ConceptMap> mappings = concept.getConceptMappings();
        return mappings
                .stream()
                .filter(c -> isExternalSystemReferenceSource(c))
                .map(c -> c.getConceptReferenceTerm().toString())
                .findFirst()
                .orElse(null);
    }

    private static String getConceptCategory(Concept concept) {
        boolean isService = (Boolean) getConceptAttributeValueByTypeUuid(concept, IS_SERVICE_CONCEPT_ATTRIBUTE_UUID, null);
        return isService ? CATEGORY_SERVICE : CATEGORY_ITEM;
    }

    private static float getConceptUnitPrice(Concept concept) {
        return (Float) getConceptAttributeValueByTypeUuid(concept, CONCEPT_PRICE_ATTRIBUTE_UUID, null);
    }

    private static boolean isExternalSystemReferenceSource(ConceptMap conceptMap) {
        return  conceptMap
                .getConceptReferenceTerm()
                .getConceptSource()
                .getUuid()
                .equals(EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_UUID);
    }

    private static Object getConceptAttributeValueByTypeUuid(Concept concept, String attributeTypeUuid, Object defaultValue) {
        return concept
                .getAttributes()
                .stream()
                .filter(c -> c.getAttributeType().getUuid().equals(attributeTypeUuid))
                .map(c -> c.getValue())
                .findFirst()
                .orElse(defaultValue);
    }
}
