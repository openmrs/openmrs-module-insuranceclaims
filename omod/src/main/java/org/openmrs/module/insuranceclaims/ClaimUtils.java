package org.openmrs.module.insuranceclaims;

import org.openmrs.Concept;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ClaimUtils {

    public static Map<String, List<InsuranceClaimItem>> getInsuranceClaimItemsAsMap(List<InsuranceClaimItem> items) {
        HashMap<String, List<InsuranceClaimItem>> itemMapping = new HashMap<>();
        for (InsuranceClaimItem item: items) {
            String name = buildItemName(item.getItem());
            itemMapping.computeIfAbsent(name, k -> new ArrayList<>());
            itemMapping.get(name).add(item);
        }
        return itemMapping;
    }

    public static Map<String, List<ProvidedItem>> getProvidedItemsAsMap(List<ProvidedItem> items) {
        Map<String, List<ProvidedItem>> itemMapping = new HashMap<>();
        for (ProvidedItem item: items) {
            String name = buildItemName(item);
            itemMapping.computeIfAbsent(name, k -> new ArrayList<>());
            itemMapping.get(name).add(item);
        }
        return itemMapping;
    }

    private static String buildItemName(ProvidedItem item) {
        String name = item.getItem() != null ? getConceptName(item.getItem()) : null;
        if (name != null) {
            name = buildKnownProvidedItemName(item);
        } else {
            name = "Unknown item " + item.hashCode();
        }
        return name;
    }

    private static String buildKnownProvidedItemName(ProvidedItem item) {
        String name = getConceptName(item.getItem());
        String attributes =  concatProvidedItemAttributes(item);
        return name + "(" + attributes + ")";
    }

    private static String concatProvidedItemAttributes(ProvidedItem item) {
        return item.getItem().getActiveAttributes().stream().map(
                attr -> attr.getValue().toString()).collect(Collectors.joining(", "));
    }

    private static String getConceptName(Concept concept) {
        return concept != null ?
                concept.getName().toString()
                : null;
    }

    private ClaimUtils() {}
}
