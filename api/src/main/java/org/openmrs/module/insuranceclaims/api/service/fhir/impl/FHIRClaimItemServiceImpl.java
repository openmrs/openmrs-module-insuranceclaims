package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Money;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONCEPT_PRICE_ATTRIBUTE_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.IS_SERVICE_CONCEPT_ATTRIBUTE_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.SpecialComponentUtil.getSpecialConditionComponentBySequenceNumber;

public class FHIRClaimItemServiceImpl implements FHIRClaimItemService {

    @Autowired
    private InsuranceClaimItemDao insuranceClaimItemDao;

    @Override
    public List<Claim.ItemComponent> generateClaimItemComponent(InsuranceClaim claim) {
        List<Claim.ItemComponent> newItemComponent = new ArrayList<>();
        List<InsuranceClaimItem> insuranceClaimItems = insuranceClaimItemDao.findInsuranceClaimItems(claim.getId());
        for (InsuranceClaimItem item: insuranceClaimItems) {
            Claim.ItemComponent next = new Claim.ItemComponent();

            next.setCategory(getItemCategory(item));
            next.setQuantity(getItemQuantity(item));
            next.setUnitPrice(getItemUnitPrice(item));
            next.setService(getItemService(item));

            newItemComponent.add(next);
        }
        return newItemComponent;
    }

    @Override
    public List<InsuranceClaimItem> generateOmrsClaimItems(Claim claim, List<String> error) {
        List<Claim.ItemComponent> items = claim.getItem();
        List<InsuranceClaimItem> insuranceClaimItems = new ArrayList<>();

        for (Claim.ItemComponent component: items) {
            try {
                InsuranceClaimItem item = generateOmrsClaimItem(component);
                String linkedExplanation = getLinkedInformation(claim, getItemComponentInformationLinkId(component));
                item.setExplanation(linkedExplanation);
                insuranceClaimItems.add(item);
            } catch (FHIRException e) {
                error.add("Could not found explanation linked to item with code "
                        + component.getService().getText());
            }

        }
        return insuranceClaimItems;
    }

    private InsuranceClaimItem generateOmrsClaimItem(Claim.ItemComponent item) {
        InsuranceClaimItem omrsItem = new InsuranceClaimItem();
        omrsItem.setQuantityProvided(getItemQuantity(item));

        ProvidedItem providedItem = new ProvidedItem();
        providedItem.setItem(findItemConcept(item));

        omrsItem.setItem(providedItem);

        return omrsItem;
    }

    public void setInsuranceClaimItemDao(InsuranceClaimItemDao insuranceClaimItemDao) {
        this.insuranceClaimItemDao = insuranceClaimItemDao;
    }

    private SimpleQuantity getItemQuantity(InsuranceClaimItem item) {
        int quantity = item.getQuantityProvided();
        SimpleQuantity itemQuantity = new SimpleQuantity();
        itemQuantity.setValue(quantity);
        return itemQuantity;
    }

    private Integer getItemQuantity(Claim.ItemComponent item) {
        if (item.getQuantity() != null) {
            return item.getQuantity().getValue().intValue();
        }
        else {
            return null;
        }
    }

    private CodeableConcept getItemCategory(InsuranceClaimItem item) {
        ProvidedItem providedItem = item.getItem();
        String category = getConceptCategory(providedItem.getItem());
        return new CodeableConcept().setText(category);
    }

    private Money getItemUnitPrice(InsuranceClaimItem item) {
        ProvidedItem providedItem = item.getItem();
        Money unitPrice = new Money();
        unitPrice.setValue(getConceptUnitPrice(providedItem.getItem()));
        return unitPrice;
    }

    private CodeableConcept getItemService(InsuranceClaimItem item) {
        ProvidedItem providedItem = item.getItem();
        String externalCode = getExternalCode(providedItem.getItem());
        return new CodeableConcept().setText(externalCode);
    }

    private String getExternalCode(Concept concept) {
        Collection<ConceptMap> mappings = concept.getConceptMappings();
        Optional<String> externalCode = mappings
                .stream()
                .filter(this::isExternalSystemReferenceSource)
                .map(c -> c.getConceptReferenceTerm().toString())
                .distinct()
                .findFirst();

        return externalCode.orElse(null);
    }

    private String getConceptCategory(Concept concept) {
        boolean isService = (Boolean) getAttributeValueByTypeUuid(concept, IS_SERVICE_CONCEPT_ATTRIBUTE_UUID, false);
        return isService ? "service" : "item";
    }

    private float getConceptUnitPrice(Concept concept) {
        return (Float) getAttributeValueByTypeUuid(concept, CONCEPT_PRICE_ATTRIBUTE_UUID, 0.0f);
    }

    private Object getAttributeValueByTypeUuid(Concept concept, String attributeTypeUuid, Object defaultValue) {
        return concept
                .getAttributes()
                .stream()
                .filter(c -> c.getAttributeType().getUuid().equals(attributeTypeUuid))
                .map(c -> c.getValue())
                .findFirst()
                .orElse(defaultValue);
    }

    private boolean isExternalSystemReferenceSource(ConceptMap conceptMap) {
        return  conceptMap.getConceptReferenceTerm().getUuid().equals(EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_UUID);
    }

    private Concept findItemConcept(Claim.ItemComponent item) {
        String itemCode = item.getService().getText();
        //TODO: Check if this data needs any validation, i.e. if unit price or category matches
        return Context.getConceptService().getConceptByMapping(itemCode, EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME);
    }

    private String getLinkedInformation(Claim claim, Integer informationSequenceId) throws FHIRException  {
        if (informationSequenceId == null) {
            return null;
        }
        return getSpecialConditionComponentBySequenceNumber(claim, informationSequenceId);
    }

    private Integer getItemComponentInformationLinkId(Claim.ItemComponent item) {
        if (item.getInformationLinkId().isEmpty()) {
            return null;
        } else {
            return item.getInformationLinkId().get(0).getValue();
        }
    }
}
