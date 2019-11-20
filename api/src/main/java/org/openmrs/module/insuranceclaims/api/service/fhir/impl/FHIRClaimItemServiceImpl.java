package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getItemCategory;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getItemQuantity;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getItemService;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getItemUnitPrice;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.SpecialComponentUtil.getSpecialConditionComponentBySequenceNumber;

public class FHIRClaimItemServiceImpl implements FHIRClaimItemService {

    @Autowired
    private InsuranceClaimItemDao insuranceClaimItemDao;

    @Autowired
    private ConceptService conceptService;

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

    public void setInsuranceClaimItemDao(InsuranceClaimItemDao insuranceClaimItemDao) {
        this.insuranceClaimItemDao = insuranceClaimItemDao;
    }

    private InsuranceClaimItem generateOmrsClaimItem(Claim.ItemComponent item) {
        InsuranceClaimItem omrsItem = new InsuranceClaimItem();
        omrsItem.setQuantityProvided(getItemQuantity(item));

        ProvidedItem providedItem = new ProvidedItem();
        providedItem.setItem(findItemConcept(item));

        omrsItem.setItem(providedItem);

        return omrsItem;
    }

    private Concept findItemConcept(Claim.ItemComponent item) {
        String itemCode = item.getService().getText();
        return conceptService.getConceptByMapping(itemCode, EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME);
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
