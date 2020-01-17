package org.openmrs.module.insuranceclaims.forms;

public interface ClaimFormBuilder {

    ValuatedClaimForm generateClaimForm(String claimUuid);
}
