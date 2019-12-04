package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Money;
import org.hl7.fhir.dstu3.model.PositiveIntType;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.db.ItemDbService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimItemService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.ClaimResponseUtil.getProcessNote;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.getUnambiguousElement;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ITEM_ADJUDICATION_GENERAL_CATEGORY;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ITEM_ADJUDICATION_REJECTION_REASON_CATEGORY;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.NEXT_SEQUENCE;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.SEQUENCE_FIRST;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.createFhirItemService;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.createItemGeneralAdjudication;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.createRejectionReasonAdjudication;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getAdjudicationRejectionReason;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getAdjudicationStatus;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getItemCategory;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getItemCodeBySequence;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getItemQuantity;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getItemUnitPrice;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.SpecialComponentUtil.getSpecialConditionComponentBySequenceNumber;

public class FHIRClaimItemServiceImpl implements FHIRClaimItemService {

    private ItemDbService itemDbService;

    @Override
    public List<Claim.ItemComponent> generateClaimItemComponent(InsuranceClaim claim) {
        List<InsuranceClaimItem> insuranceClaimItems = itemDbService.findInsuranceClaimItems(claim.getId());
        return generateClaimItemComponent(insuranceClaimItems);
    }

    @Override
    public List<Claim.ItemComponent> generateClaimItemComponent(List<InsuranceClaimItem> insuranceClaimItems) {
        List<Claim.ItemComponent> newItemComponent = new ArrayList<>();
        for (InsuranceClaimItem item: insuranceClaimItems) {
            Claim.ItemComponent next = new Claim.ItemComponent();

            next.setCategory(getItemCategory(item));
            next.setQuantity(getItemQuantity(item));
            next.setUnitPrice(getItemUnitPrice(item));
            next.setService(createFhirItemService(item));
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

    @Override
    public List<ClaimResponse.ItemComponent> generateClaimResponseItemComponent(InsuranceClaim claim) {
        List<ClaimResponse.ItemComponent> items = new ArrayList<>();
        List<InsuranceClaimItem> insuranceClaimItems = itemDbService.findInsuranceClaimItems(claim.getId());

        int sequence = SEQUENCE_FIRST;
        for (InsuranceClaimItem insuranceClaimItem: insuranceClaimItems) {
            ClaimResponse.ItemComponent nextItem = new ClaimResponse.ItemComponent();
            //General
            nextItem.addAdjudication(createItemGeneralAdjudication(insuranceClaimItem));
            //Rejection Reason
            nextItem.addAdjudication(createRejectionReasonAdjudication(insuranceClaimItem));

            nextItem.setSequenceLinkId(sequence);
            nextItem.addNoteNumber(sequence);
            sequence += NEXT_SEQUENCE;

            items.add(nextItem);
        }
        return items;
    }

    @Override
    public List<InsuranceClaimItem> generateOmrsClaimResponseItems(ClaimResponse claim, List<String> error) {
        List<InsuranceClaimItem> omrsItems = new ArrayList<>();
        for (ClaimResponse.ItemComponent item: claim.getItem()) {
            InsuranceClaimItem nextItem = new InsuranceClaimItem();
            try {
                //Item
                List<String> itemCodes = getItemCodeBySequence(claim, item.getSequenceLinkId());
                nextItem.setItem(generateProvidedItem(itemCodes));
            } catch (FHIRException exception) {
                error.add(exception.getMessage());
            }
            //Adjudication
            for (ClaimResponse.AdjudicationComponent adjudicationComponent: item.getAdjudication()) {
                CodeableConcept adjudicationCategoryCoding = adjudicationComponent.getCategory();
                String adjudicationCode = adjudicationCategoryCoding.getText();
                if (adjudicationCode.equals(ITEM_ADJUDICATION_GENERAL_CATEGORY)) {
                    nextItem.setQuantityApproved(getAdjudicationQuantityApproved(adjudicationComponent));
                    nextItem.setPriceApproved(getAdjudicationPriceApproved(adjudicationComponent));
                    nextItem.setStatus(getAdjudicationStatus(adjudicationComponent));
                } else {
                    if (adjudicationCode.equals(ITEM_ADJUDICATION_REJECTION_REASON_CATEGORY)) {
                        String reason = getAdjudicationRejectionReason(adjudicationComponent);
                        nextItem.setRejectionReason(StringUtils.isNotEmpty(reason) ? reason : "None");
                    } else {
                        error.add("Cound not found strategy for adjudication code " + adjudicationCode);
                    }
                }
            }
            //Justification
            nextItem.setJustification(getProcessNote(claim, getFirstItemNoteNumber(item)));

            omrsItems.add(nextItem);
        }

        return omrsItems;
    }

    @Override
    public List<ClaimResponse.NoteComponent> generateClaimResponseNotes(InsuranceClaim claim)  {
        List<ClaimResponse.NoteComponent> claimNotes = new ArrayList<>();
        List<InsuranceClaimItem> items = itemDbService.findInsuranceClaimItems(claim.getId());
        int noteNumber = SEQUENCE_FIRST;
        for (InsuranceClaimItem item: items) {
            ClaimResponse.NoteComponent nextNote = new ClaimResponse.NoteComponent();
            nextNote.setText(item.getJustification());
            nextNote.setNumber(noteNumber);

            claimNotes.add(nextNote);
            noteNumber += NEXT_SEQUENCE;
        }
        return claimNotes;
    }

    public void setItemDbService(ItemDbService insuranceClaimItemDao) {
        this.itemDbService = insuranceClaimItemDao;
    }

    private ProvidedItem generateProvidedItem(List<String> itemCodes) {
        ProvidedItem providedItem = new ProvidedItem();
        providedItem.setItem(getConceptByExternalId(itemCodes));
        return providedItem;
    }

    private Concept getConceptByExternalId(List<String> itemCodes) {
        List<Concept> conceptList = itemCodes.stream()
                .map(code -> Context.getConceptService().getConceptByMapping(code, EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME))
                .collect(Collectors.toList());
        return getUnambiguousElement(conceptList);
    }

    private InsuranceClaimItem generateOmrsClaimItem(Claim.ItemComponent item) throws FHIRException {
        InsuranceClaimItem omrsItem = new InsuranceClaimItem();
        String itemCode = item.getService().getText();
        ProvidedItem providedItem = generateProvidedItem(Collections.singletonList(itemCode));
        omrsItem.setQuantityProvided(getItemQuantity(item));
        omrsItem.setItem(providedItem);
        if (providedItem.getItem() == null) {
            throw new FHIRException("Could not find object related to code" + itemCode);
        }
        return omrsItem;
    }

    private String getLinkedInformation(Claim claim, Integer informationSequenceId) throws FHIRException {
        return informationSequenceId == null ? null : getSpecialConditionComponentBySequenceNumber(claim, informationSequenceId);
    }

    private Integer getItemComponentInformationLinkId(Claim.ItemComponent item) {
       return CollectionUtils.isEmpty(item.getInformationLinkId()) ?
               null : getUnambiguousElement(item.getInformationLinkId()).getValue();
    }

    private Integer getFirstItemNoteNumber(ClaimResponse.ItemComponent item) {
        PositiveIntType note = getUnambiguousElement(item.getNoteNumber());
        return note != null ? note.getValue() : null;
    }

    private Integer getAdjudicationQuantityApproved(ClaimResponse.AdjudicationComponent component) {
        BigDecimal approved = component.getValue();
        return approved != null ? approved.intValue() : null;
    }

    private BigDecimal getAdjudicationPriceApproved(ClaimResponse.AdjudicationComponent component) {
        Money approved = component.getAmount();
        return approved != null ? approved.getValue() : null;
    }
}
