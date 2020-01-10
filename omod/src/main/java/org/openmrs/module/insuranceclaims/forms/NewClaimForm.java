package org.openmrs.module.insuranceclaims.forms;

import java.util.List;
import java.util.Map;

/**
 * Class used as storage for data used in creating new insurance claim
 */
public class NewClaimForm {

    private Map<String, ProvidedItemInForm> providedItems;
    private String claimExplanation;
    private String claimJustification;
    private String startDate;
    private String endDate;
    private String location;
    private List<String> diagnoses;
    private boolean paidInFacility;
    private String patient;
    private String visitType;
    private String guaranteeId;
    private String provider;
    private String claimCode;

    public String getGuaranteeId() {
        return guaranteeId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Map<String, ProvidedItemInForm> getProvidedItems() {
        return this.providedItems;
    }

    public String getClaimExplanation() {
        return claimExplanation;
    }

    public String getClaimJustification() {
        return claimJustification;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getDiagnoses() {
        return diagnoses;
    }

    public String getPatient() {
        return patient;
    }

    public String getVisitType() {
        return visitType;
    }

    public boolean isPaidInFacility() {
        return paidInFacility;
    }

    public String getProvider() {
        return provider;
    }

    public String getClaimCode() {
        return claimCode;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setProvidedItems(Map<String, ProvidedItemInForm> providedItems) {
        this.providedItems = providedItems;
    }

    public void setClaimExplanation(String claimExplanation) {
        this.claimExplanation = claimExplanation;
    }

    public void setClaimJustification(String claimJustification) {
        this.claimJustification = claimJustification;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDiagnoses(List<String> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public void setPaidInFacility(boolean paidInFacility) {
        this.paidInFacility = paidInFacility;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public void setGuaranteeId(String guaranteeId) {
        this.guaranteeId = guaranteeId;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setClaimCode(String claimCode) {
        this.claimCode = claimCode;
    }
}

