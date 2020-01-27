package org.openmrs.module.insuranceclaims.api.service.impl;

import org.hl7.fhir.dstu3.model.Money;
import org.openmrs.Concept;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItemStatus;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimItemService;
import org.openmrs.module.insuranceclaims.api.service.exceptions.ItemMatchingFailedException;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

public class InsuranceClaimItemServiceImpl extends BaseOpenmrsDataService<InsuranceClaimItem>
        implements InsuranceClaimItemService {

    @Override
    @Transactional
    public InsuranceClaimItem updateInsuranceClaimItem(InsuranceClaimItem itemToUpdate, InsuranceClaimItem itemUpdated) {
        itemToUpdate.setStatus(itemUpdated.getStatus());
        itemToUpdate.setRejectionReason(itemUpdated.getRejectionReason());
        itemToUpdate.setJustification(itemUpdated.getJustification());
        updateQuantityApproved(itemToUpdate, itemUpdated);
        updatePriceApproved(itemToUpdate, itemUpdated);
        saveOrUpdate(itemToUpdate);

        return itemToUpdate;
    }

    @Override
    @Transactional
    public List<InsuranceClaimItem> updateInsuranceClaimItems(List<InsuranceClaimItem> itemsToUpdate,
            List<InsuranceClaimItem> itemsWithUpdates) throws ItemMatchingFailedException {
        List<InsuranceClaimItem> itemsUpdatedCopy = new ArrayList<>(itemsWithUpdates);
        for (InsuranceClaimItem item: itemsToUpdate) {
            String itemCode = getClaimItemCode(item);
            int quantityProvided = item.getQuantityProvided();
            Money unitPrice = InsuranceClaimItemUtil.getItemUnitPrice(item);
            InsuranceClaimItem matchingItemUpdated = findFirstMatchingItem(itemCode, quantityProvided, unitPrice, itemsUpdatedCopy);
            updateInsuranceClaimItem(item, matchingItemUpdated);
            itemsUpdatedCopy.remove(matchingItemUpdated);
        }

        if (!itemsUpdatedCopy.isEmpty()) {
            String unusedUpdatedItemCodes = itemsUpdatedCopy.stream()
                    .map(item -> getClaimItemCode(item)).collect(Collectors.joining());
            throw new ItemMatchingFailedException("Could not update items, failed to match updated item with codes: " +
                    unusedUpdatedItemCodes);
        }

        itemsToUpdate.forEach(this::saveOrUpdate);
        return itemsToUpdate;
    }

    private String getClaimItemCode(InsuranceClaimItem item) {
        Concept itemConcept = item.getItem().getItem();
        return InsuranceClaimItemUtil.getExternalCode(itemConcept);
    }

    private void updateQuantityApproved(InsuranceClaimItem itemToUpdate, InsuranceClaimItem itemWithUpdate) {
        Integer quantityApproved = itemWithUpdate.getQuantityApproved();
        InsuranceClaimItemStatus status = itemWithUpdate.getStatus();
        if (quantityApproved == null && status == InsuranceClaimItemStatus.PASSED) {
            itemToUpdate.setQuantityApproved(itemToUpdate.getQuantityProvided());
        } else {
            itemToUpdate.setQuantityApproved(quantityApproved);
        }
    }

    private void updatePriceApproved(InsuranceClaimItem itemToUpdate, InsuranceClaimItem itemWithUpdate) {
        BigDecimal priceApproved = itemWithUpdate.getPriceApproved();
        InsuranceClaimItemStatus status = itemWithUpdate.getStatus();
        if (priceApproved == null && status == InsuranceClaimItemStatus.PASSED) {
            Money conceptPrice = InsuranceClaimItemUtil.getItemUnitPrice(itemToUpdate);
            itemToUpdate.setPriceApproved(conceptPrice.getValue());
        } else {
            itemToUpdate.setPriceApproved(priceApproved);
        }
    }

    private InsuranceClaimItem findFirstMatchingItem(String itemExternalCode, int quantityProvided, Money unitPrice,
            List<InsuranceClaimItem> items) throws ItemMatchingFailedException {
        return items.stream()
                .filter(item -> getClaimItemCode(item).equals(itemExternalCode))
                .filter(item -> item.getQuantityProvided() == quantityProvided)
                .filter(item -> InsuranceClaimItemUtil.getItemUnitPrice(item).equalsDeep(unitPrice))
                .findFirst()
                .orElseThrow(() -> new ItemMatchingFailedException("Could not find match for item with code " +
                        itemExternalCode));
    }
}
