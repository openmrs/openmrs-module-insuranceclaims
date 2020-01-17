package org.openmrs.module.insuranceclaims.forms;

import ca.uhn.fhir.util.DateUtils;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;

import static org.openmrs.module.insuranceclaims.ClaimUtils.buildItemName;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.EXPECTED_DATE_PATTERN;

public class ValuatedClaimItem {
    private String itemName;
    private String itemUuid;
    private String dateServed;
    private String explanation;
    private InsuranceClaimItem item;

    public ValuatedClaimItem() {}

    public ValuatedClaimItem(InsuranceClaimItem item) {
        this.item = item;
        this.itemName = buildItemName(item.getItem());
        this.itemUuid = item.getUuid();
        this.explanation = item.getExplanation();
        this.dateServed = DateUtils.formatDate(item.getItem().getDateOfServed(), EXPECTED_DATE_PATTERN);
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
    }

    public void setDateServed(String dateServed) {
        this.dateServed = dateServed;
    }

    public String getDateServed() {
        return dateServed;
    }

    public InsuranceClaimItem getItem() {
        return this.item;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getExplanation() {
        return this.explanation;
    }
}
