package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimStatus;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;

import java.math.BigDecimal;

public class InsuranceClaimServiceImpl extends BaseOpenmrsDataService<InsuranceClaim> implements InsuranceClaimService {

    public InsuranceClaim updateClaim(InsuranceClaim claimToUpdate, InsuranceClaim updatedClaim) {
        claimToUpdate.setAdjustment(updatedClaim.getAdjustment());
        updateQuantityApproved(claimToUpdate, updatedClaim);
        claimToUpdate.setDateProcessed(updatedClaim.getDateProcessed());
        claimToUpdate.setRejectionReason(updatedClaim.getRejectionReason());
        claimToUpdate.setStatus(updatedClaim.getStatus());

        saveOrUpdate(claimToUpdate);

        return claimToUpdate;
    }

    private void updateQuantityApproved(InsuranceClaim claimToUpdate, InsuranceClaim updatedClaim) {
        BigDecimal totalBenefit = updatedClaim.getApprovedTotal();
        InsuranceClaimStatus status = updatedClaim.getStatus();
        if (totalBenefit == null && status == InsuranceClaimStatus.CHECKED) {
            claimToUpdate.setApprovedTotal(claimToUpdate.getClaimedTotal());
        } else {
            claimToUpdate.setApprovedTotal(totalBenefit);
        }
    }
}
