package org.openmrs.module.insuranceclaims.api.model;

import java.util.List;

public class ProvidedItemInForm {
    private List<String> items;
    private String explanation;
    private String justification;

    public List<String> getItems() {
        return items;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getJustification() {
        return justification;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
