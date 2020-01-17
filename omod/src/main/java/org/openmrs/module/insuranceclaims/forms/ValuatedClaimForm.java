package org.openmrs.module.insuranceclaims.forms;

import ca.uhn.fhir.util.DateUtils;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import java.util.List;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.EXPECTED_DATE_PATTERN;

public class ValuatedClaimForm extends NewClaimForm {
    private List<ValuatedClaimItem> claimItems;
    private List<ValuatedClaimDiagnosis> claimDiagnoses;
    private InsuranceClaim claim;

    public ValuatedClaimForm(InsuranceClaim claim) {
        this.claim = claim;
        setClaimCode(claim.getClaimCode());
        setClaimExplanation(claim.getExplanation());
        setClaimJustification(claim.getAdjustment());
        setStartDate(DateUtils.formatDate(claim.getDateFrom(), EXPECTED_DATE_PATTERN));
        setEndDate(DateUtils.formatDate(claim.getDateTo(), EXPECTED_DATE_PATTERN));
        setLocation(claim.getLocation().getId().toString());

        setPaidInFacility(false);
        setPatient(claim.getPatient().getId().toString());
        setVisitType(claim.getVisitType().getName());
        setGuaranteeId(claim.getGuaranteeId());
        setProvider(claim.getProvider().getUuid());
    }
    public void setClaimItems(List<ValuatedClaimItem> claimItems) {
        this.claimItems = claimItems;
    }

    public List<ValuatedClaimItem> getClaimItems() {
        return claimItems;
    }

    public void setClaimDiagnoses(List<ValuatedClaimDiagnosis> claimDiagnoses) {
        this.claimDiagnoses = claimDiagnoses;
    }

    public List<ValuatedClaimDiagnosis> getClaimDiagnoses() {
        return claimDiagnoses;
    }

    public InsuranceClaim getClaim() {
        return claim;
    }

    public void setClaim(InsuranceClaim claim) {
        this.claim = claim;
    }
}
