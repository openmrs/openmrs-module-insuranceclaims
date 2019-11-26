package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Money;
import org.hl7.fhir.dstu3.model.PositiveIntType;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItemStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.getUnambiguousElement;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ITEM_ADJUDICATION_GENERAL_CATEGORY;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ITEM_ADJUDICATION_REJECTION_REASON_CATEGORY;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.SEQUENCE_FIRST;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.createFhirItemService;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getItemCategory;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.getItemQuantity;
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
        List<InsuranceClaimItem> insuranceClaimItems = insuranceClaimItemDao.findInsuranceClaimItems(claim.getId());

        int sequence = SEQUENCE_FIRST;
        for (InsuranceClaimItem insuranceClaimItem: insuranceClaimItems) {
            ClaimResponse.ItemComponent nextItem = new ClaimResponse.ItemComponent();
            //General
            nextItem.addAdjudication(createItemGeneralAdjudication(insuranceClaimItem));
            //Rejection Reason
            nextItem.addAdjudication(createRejectionReasonAdjudication(insuranceClaimItem));

            nextItem.setSequenceLinkId(sequence);
            nextItem.addNoteNumber(sequence);
            sequence += 1;

            items.add(nextItem);
            //TODO: Add processNotes
        }
        return items;
    }

    @Override
    public List<InsuranceClaimItem> generateOmrsClaimResponseItems(ClaimResponse claim, List<String> error) throws FHIRException {
        List<InsuranceClaimItem> omrsItems = new ArrayList<>();
        for (ClaimResponse.ItemComponent item: claim.getItem()) {
            InsuranceClaimItem nextItem = new InsuranceClaimItem();
            List<String> itemCodes = getItemCodeBySequence(claim, item.getSequenceLinkId());

            //Item
            nextItem.setItem(generateProvidedItem(itemCodes));

            //Adjudication
            for (ClaimResponse.AdjudicationComponent adjudicationComponent: item.getAdjudication()) {
                CodeableConcept adjudicationCategoryCoding = adjudicationComponent.getCategory();
                String adjudicationCode = adjudicationCategoryCoding.getText();
                if (adjudicationCode.equals(ITEM_ADJUDICATION_GENERAL_CATEGORY)) {
                    nextItem.setQuantityApproved(adjudicationComponent.getValue().intValue());
                    nextItem.setPriceApproved(adjudicationComponent.getAmount().getValue());
                    nextItem.setStatus(getAdjudicationStatus(adjudicationComponent));
                }
                if (adjudicationCode.equals(ITEM_ADJUDICATION_REJECTION_REASON_CATEGORY)) {
                    nextItem.setRejectionReason(getAdjudicationRejectionReason(adjudicationComponent));
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
        List<InsuranceClaimItem> items = insuranceClaimItemDao.findInsuranceClaimItems(claim.getId());
        int noteNumber = SEQUENCE_FIRST;
        for (InsuranceClaimItem item: items) {
            ClaimResponse.NoteComponent nextNote = new ClaimResponse.NoteComponent();
            nextNote.setText(item.getJustification());
            nextNote.setNumber(noteNumber);

            claimNotes.add(nextNote);
            noteNumber += 1;
        }
        return claimNotes;
    }

    @Override
    public void setInsuranceClaimItemDao(InsuranceClaimItemDao insuranceClaimItemDao) {
        this.insuranceClaimItemDao = insuranceClaimItemDao;
    }

    private String getProcessNote(ClaimResponse response, int noteNumber) {
        List<ClaimResponse.NoteComponent> notes = response.getProcessNote();

        return notes.stream()
                .filter(note -> note.getNumber() == noteNumber)
                .findFirst()
                .map(ClaimResponse.NoteComponent::getText)
                .orElse(null);
    }

    private InsuranceClaimItemStatus getAdjudicationStatus(ClaimResponse.AdjudicationComponent adjudicationComponent) {
        Coding reasonCoding = adjudicationComponent.getReason().getCodingFirstRep();
        return InsuranceClaimItemStatus.valueOf(reasonCoding.getSystem());
    }

    private String getAdjudicationRejectionReason(ClaimResponse.AdjudicationComponent adjudicationComponent) {
        Coding reasonCoding = adjudicationComponent.getReason().getCodingFirstRep();
        return reasonCoding.getCode();
    }

    private ProvidedItem generateProvidedItem(List<String> itemCodes) {
        ProvidedItem providedItem = new ProvidedItem();
        providedItem.setItem(getConceptByExternalId(itemCodes));
        return providedItem;
    }

    private Concept getConceptByExternalId(List<String> itemCodes) {
        List<Concept> conceptList = itemCodes.stream()
                .map(code -> conceptService.getConceptByMapping(code, EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME))
                .collect(Collectors.toList());

        return getUnambiguousElement(conceptList);
    }

    private int getItemQuantityApproved(InsuranceClaimItem insuranceClaimItem) {
        return insuranceClaimItem.getQuantityApproved();
    }

    private ClaimResponse.AdjudicationComponent createItemGeneralAdjudication(
            InsuranceClaimItem insuranceClaimItem) {
        ClaimResponse.AdjudicationComponent adjudication = new ClaimResponse.AdjudicationComponent();
        adjudication.setReason(getItemStatusReason(insuranceClaimItem)); //Set reason
        adjudication.setValue(getItemQuantityApproved(insuranceClaimItem)); //Set value
        adjudication.setAmount(getItemAmount(insuranceClaimItem)); //Set amount
        adjudication.setCategory(getReasonCategory(ITEM_ADJUDICATION_GENERAL_CATEGORY)); //setCategory
        return adjudication;
    }

    private ClaimResponse.AdjudicationComponent createRejectionReasonAdjudication(
            InsuranceClaimItem insuranceClaimItem) {
        ClaimResponse.AdjudicationComponent adjudicationComponent = new ClaimResponse.AdjudicationComponent();
        adjudicationComponent.setReason(getItemRejectionReason(insuranceClaimItem));
        adjudicationComponent.setCategory(getReasonCategory(ITEM_ADJUDICATION_REJECTION_REASON_CATEGORY));
        return adjudicationComponent;
    }

    private CodeableConcept getItemStatusReason(InsuranceClaimItem insuranceClaimItem) {
        InsuranceClaimItemStatus status = insuranceClaimItem.getStatus();
        CodeableConcept reason = new CodeableConcept();
        Coding reasonCoding = new Coding();
        reasonCoding.setCode(String.valueOf(status.ordinal()));
        reasonCoding.setSystem(status.toString());
        reason.addCoding(reasonCoding);
        return reason;
    }

    private CodeableConcept getItemRejectionReason(InsuranceClaimItem insuranceClaimItem) {
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setCode(insuranceClaimItem.getRejectionReason());
        codeableConcept.addCoding(coding);
        return codeableConcept;
    }

    private Money getItemAmount(InsuranceClaimItem insuranceClaimItem) {
        Money amount = new Money();
        amount.setValue(insuranceClaimItem.getPriceApproved());
        return amount;
    }

    private CodeableConcept getReasonCategory(String categoryName) {
        CodeableConcept categoryConcept = new CodeableConcept();
        categoryConcept.setText(categoryName);
        return categoryConcept;
    }

    private List<String> getItemCodeBySequence(ClaimResponse response, int sequenceId) throws FHIRException {
        List<ClaimResponse.AddedItemComponent> addedItemComponents = response.getAddItem();

        ClaimResponse.AddedItemComponent correspondingAdditem = addedItemComponents.stream()
                .filter(addItem -> isValueInSequence(addItem.getSequenceLinkId(), sequenceId))
                .findFirst()
                .orElse(null);

        if (correspondingAdditem == null) {
            throw new FHIRException("No item code corresponding to " + sequenceId + " found");
        }

        List<Coding> itemCoding = correspondingAdditem.getService().getCoding();
        return itemCoding.stream()
                .map(Coding::getCode)
                .collect(Collectors.toList());
    }

    private InsuranceClaimItem generateOmrsClaimItem(Claim.ItemComponent item) {
        InsuranceClaimItem omrsItem = new InsuranceClaimItem();
        omrsItem.setQuantityProvided(getItemQuantity(item));

        ProvidedItem providedItem = new ProvidedItem();
        providedItem.setItem(findItemConcept(item));

        omrsItem.setItem(providedItem);

        return omrsItem;
    }

    private boolean isValueInSequence(List<PositiveIntType> sequence, int sequenceLinkId) {
        return sequence.stream().map(PrimitiveType::getValue)
                .anyMatch(value -> value.equals(sequenceLinkId));
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

    private int getFirstItemNoteNumber(ClaimResponse.ItemComponent item) {
        return item.getNoteNumber().get(0).getValue();
    }
}
