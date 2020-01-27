package org.openmrs.module.insuranceclaims.api.mapper;

import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.model.dto.InsurancePolicyDTO;

public class InsurancePolicyMapper extends AbstractMapper<InsurancePolicyDTO, InsurancePolicy> {

    @Override
    public InsurancePolicyDTO toDto(InsurancePolicy dao) {
        return new InsurancePolicyDTO()
                .setExpiryDate(dao.getExpiryDate())
                .setAllowedMoney(dao.getAllowedMoney())
                .setPolicyNumber(dao.getPolicyNumber())
                .setStatus(dao.getStatus().name());
    }
}
