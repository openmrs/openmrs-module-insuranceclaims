package org.openmrs.module.insuranceclaims;

import org.openmrs.Concept;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ClaimUtils {

    public static Map<String, InsuranceClaimItem> getInsuranceClaimItemsAsMap(List<InsuranceClaimItem> items) {
        Map<String, InsuranceClaimItem> itemMapping = new HashMap<>();
        items.forEach(
                i -> {
                    String name = i.getItem() != null ? getConceptName(i.getItem().getItem()) : null;
                    if (name != null) {
                        String attributes =  i.getItem().getItem().getActiveAttributes().stream().map(
                                attr -> attr.getValue().toString() + ", "
                        ).collect(Collectors.joining());
                        name += "(" + attributes + ")";
                    } else {
                        name = "Unknown item " + i.hashCode();
                    }
                    itemMapping.put(name, i);
                }
        );
        return itemMapping;
    }

    public static Map<String, List<ProvidedItem>> getProvidedItemsAsMap(List<ProvidedItem> items) {
        Map<String, List<ProvidedItem>> itemMapping = new HashMap<>();
        items.forEach(
                i -> {
                    String name = i.getItem() != null ? getConceptName(i.getItem()) : null;
                    if (name != null) {
                        String attributes =  i.getItem().getActiveAttributes().stream().map(
                                attr -> attr.getValue().toString() + ", "
                        ).collect(Collectors.joining());
                        name += "(" + attributes + ")";
                    } else {
                        name = "Unknown item " + i.hashCode();
                    }
                    if (itemMapping.get(name) == null) {
                        List<ProvidedItem> newList = new LinkedList<>();
                        newList.add(i);
                        itemMapping.put(name, newList);
                    } else {
                        itemMapping.get(name).add(i);
                    }
                }
        );
        return itemMapping;
    }

    private static String getConceptName(Concept concept) {
        return concept != null ?
                concept.getName().toString()
                : null;
    }


    private ClaimUtils() {}

}
