package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Money;
import org.hl7.fhir.dstu3.model.PositiveIntType;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItemStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CATEGORY_ITEM;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CATEGORY_SERVICE;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONCEPT_PRICE_ATTRIBUTE_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.IS_SERVICE_CONCEPT_ATTRIBUTE_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ITEM_ADJUDICATION_GENERAL_CATEGORY;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ITEM_ADJUDICATION_REJECTION_REASON_CATEGORY;

public final class InsuranceClaimItemUtil {
    public static SimpleQuantity getItemQuantity(InsuranceClaimItem item) {
        int quantity = item.getQuantityProvided();
        SimpleQuantity itemQuantity = new SimpleQuantity();
        itemQuantity.setValue(quantity);
        return itemQuantity;
    }

    public static Integer getItemQuantity(Claim.ItemComponent item) {
        return item.getQuantity() != null ?  item.getQuantity().getValue().intValue() : null;
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

    public static InsuranceClaimItemStatus getAdjudicationStatus(ClaimResponse.AdjudicationComponent adjudicationComponent) {
        CodeableConcept reasonConcept = adjudicationComponent.getReason();
        String adjustedReason = getAdjustedReason(reasonConcept);
        adjustedReason = StringUtils.upperCase(adjustedReason);
        return InsuranceClaimItemStatus.valueOf(adjustedReason);
    }

    public static String getAdjudicationRejectionReason(ClaimResponse.AdjudicationComponent adjudicationComponent) {
        Coding reasonCoding = adjudicationComponent.getReason().getCodingFirstRep();
        return reasonCoding.getCode();
    }

    public static ClaimResponse.AdjudicationComponent createItemGeneralAdjudication(
            InsuranceClaimItem insuranceClaimItem) {
        ClaimResponse.AdjudicationComponent adjudication = new ClaimResponse.AdjudicationComponent();
        adjudication.setReason(getItemStatusReason(insuranceClaimItem)); //Set reason
        adjudication.setValue(getItemQuantityApproved(insuranceClaimItem)); //Set value
        adjudication.setAmount(getItemAmount(insuranceClaimItem)); //Set amount
        adjudication.setCategory(getReasonCategory(ITEM_ADJUDICATION_GENERAL_CATEGORY)); //setCategory
        return adjudication;
    }

    public static ClaimResponse.AdjudicationComponent createRejectionReasonAdjudication(
            InsuranceClaimItem insuranceClaimItem) {
        ClaimResponse.AdjudicationComponent adjudicationComponent = new ClaimResponse.AdjudicationComponent();
        adjudicationComponent.setReason(getItemRejectionReason(insuranceClaimItem));
        adjudicationComponent.setCategory(getReasonCategory(ITEM_ADJUDICATION_REJECTION_REASON_CATEGORY));
        return adjudicationComponent;
    }

    public static CodeableConcept getItemStatusReason(InsuranceClaimItem insuranceClaimItem) {
        InsuranceClaimItemStatus status = insuranceClaimItem.getStatus();
        CodeableConcept reason = new CodeableConcept();
        Coding reasonCoding = new Coding();
        reasonCoding.setCode(String.valueOf(status.ordinal()));
        reasonCoding.setSystem(status.toString());
        reason.addCoding(reasonCoding);
        reason.setText(status.name());
        return reason;
    }

    public static CodeableConcept getItemRejectionReason(InsuranceClaimItem insuranceClaimItem) {
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setCode(insuranceClaimItem.getRejectionReason());
        codeableConcept.addCoding(coding);
        return codeableConcept;
    }

    public static Money getItemAmount(InsuranceClaimItem insuranceClaimItem) {
        Money amount = new Money();
        amount.setValue(insuranceClaimItem.getPriceApproved());
        return amount;
    }

    public static int getItemQuantityApproved(InsuranceClaimItem insuranceClaimItem) {
        return insuranceClaimItem.getQuantityApproved();
    }

    public static CodeableConcept getReasonCategory(String categoryName) {
        CodeableConcept categoryConcept = new CodeableConcept();
        categoryConcept.setText(categoryName);
        return categoryConcept;
    }

    public static List<String> getItemCodeBySequence(ClaimResponse response, int sequenceId) throws FHIRException {
        List<ClaimResponse.AddedItemComponent> addedItemComponents = response.getAddItem();

        ClaimResponse.AddedItemComponent correspondingAddItem = addedItemComponents.stream()
                .filter(addItem -> isValueInSequence(addItem.getSequenceLinkId(), sequenceId))
                .findFirst()
                .orElse(null);

        if (correspondingAddItem == null) {
            throw new FHIRException("No item code corresponding to " + sequenceId + " found");
        }

        List<Coding> itemCoding = correspondingAddItem.getService().getCoding();
        return itemCoding.stream()
                .map(Coding::getCode)
                .collect(Collectors.toList());
    }

    public static String getExternalCode(Concept concept) {
        Collection<ConceptMap> mappings = concept.getConceptMappings();
        return mappings
                .stream()
                .filter(c -> isExternalSystemReferenceSource(c))
                .map(c -> c.getConceptReferenceTerm().getCode())
                .findFirst()
                .orElse(null);
    }

    private static String getAdjustedReason(CodeableConcept concept) {
        return StringUtils.upperCase(concept.getText());
    }

    private static boolean isValueInSequence(List<PositiveIntType> sequence, int sequenceLinkId) {
        return sequence.stream().map(PrimitiveType::getValue)
                .anyMatch(value -> value.equals(sequenceLinkId));
    }

    private static String getConceptCategory(Concept concept) {
        Boolean isService = (Boolean) getConceptAttributeValueByTypeUuid(concept, IS_SERVICE_CONCEPT_ATTRIBUTE_UUID);
        return isService ? CATEGORY_SERVICE : CATEGORY_ITEM;
    }

    private static float getConceptUnitPrice(Concept concept) {
        return (Float) getConceptAttributeValueByTypeUuid(concept, CONCEPT_PRICE_ATTRIBUTE_UUID);
    }

    private static boolean isExternalSystemReferenceSource(ConceptMap conceptMap) {
        return  conceptMap
                .getConceptReferenceTerm()
                .getConceptSource()
                .getName()
                .equals(EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME);
    }

    private static Object getConceptAttributeValueByTypeUuid(Concept concept, String attributeTypeUuid) {
        return concept
                .getAttributes()
                .stream()
                .filter(c -> c.getAttributeType().getUuid().equals(attributeTypeUuid))
                .map(c -> c.getValue())
                .findFirst()
                .orElse(null);
    }
}
