package org.openmrs.module.insuranceclaims.api.client.impl;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;

import java.util.List;

public class ClaimRequestWrapper {

    private InsuranceClaim insuranceClaim;
    private List<InsuranceClaimDiagnosis> diagnosis;
    private List<InsuranceClaimItem> items;
    private List<String> errors;

    public ClaimRequestWrapper(InsuranceClaim claim, List<InsuranceClaimDiagnosis> diagnosis,
    List<InsuranceClaimItem> items) {
        this.insuranceClaim = claim;
        this.diagnosis = diagnosis;
        this.items = items;
    }

    public ClaimRequestWrapper(InsuranceClaim claim, List<InsuranceClaimDiagnosis> diagnosis,
    List<InsuranceClaimItem> items, List<String> errors) {
        this.insuranceClaim = claim;
        this.diagnosis = diagnosis;
        this.items = items;
        this.errors = errors;
    }

    public InsuranceClaim getInsuranceClaim() {
        return insuranceClaim;
    }

    public void setInsuranceClaim(InsuranceClaim insuranceClaim) {
        this.insuranceClaim = insuranceClaim;
    }

    public List<InsuranceClaimDiagnosis> getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(List<InsuranceClaimDiagnosis> diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void addDiagnosis(InsuranceClaimDiagnosis diagnosis) {
        this.diagnosis.add(diagnosis);
    }

    public List<InsuranceClaimItem> getItems() {
        return items;
    }

    public void setItems(List<InsuranceClaimItem> items) {
        this.items = items;
    }

    public void addItem(InsuranceClaimItem item) {
        this.items.add(item);
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
