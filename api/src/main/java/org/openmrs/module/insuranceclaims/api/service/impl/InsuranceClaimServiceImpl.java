package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimStatus;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

public class InsuranceClaimServiceImpl extends BaseOpenmrsDataService<InsuranceClaim> implements InsuranceClaimService {
    private InsuranceClaimDao insuranceClaimDao;

    public void setInsuranceClaimDao(InsuranceClaimDao insuranceClaimDao) {
        this.insuranceClaimDao = insuranceClaimDao;
    }

    @Transactional
    @Override
    public InsuranceClaim updateClaim(InsuranceClaim claimToUpdate, InsuranceClaim updatedClaim) {
        claimToUpdate.setAdjustment(updatedClaim.getAdjustment());
        updateQuantityApproved(claimToUpdate, updatedClaim);
        claimToUpdate.setDateProcessed(updatedClaim.getDateProcessed());
        claimToUpdate.setRejectionReason(updatedClaim.getRejectionReason());
        claimToUpdate.setStatus(updatedClaim.getStatus());

        saveOrUpdate(claimToUpdate);

        return claimToUpdate;
    }

    @Transactional
    @Override
    public List<InsuranceClaim> getAllInsuranceClaims(Integer patientId) throws APIException {
        return this.insuranceClaimDao.getAllInsuranceClaims(patientId);
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
