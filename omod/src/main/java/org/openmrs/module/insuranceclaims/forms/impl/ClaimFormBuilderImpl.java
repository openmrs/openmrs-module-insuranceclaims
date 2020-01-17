package org.openmrs.module.insuranceclaims.forms.impl;


import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.service.db.DiagnosisDbService;
import org.openmrs.module.insuranceclaims.api.service.db.ItemDbService;
import org.openmrs.module.insuranceclaims.forms.ClaimFormBuilder;
import org.openmrs.module.insuranceclaims.forms.ValuatedClaimDiagnosis;
import org.openmrs.module.insuranceclaims.forms.ValuatedClaimForm;
import org.openmrs.module.insuranceclaims.forms.ValuatedClaimItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimFormBuilderImpl implements ClaimFormBuilder {

    @Autowired
    InsuranceClaimService insuranceClaimService;

    @Autowired
    ItemDbService itemDbService;

    @Autowired
    DiagnosisDbService diagnosisDbService;

    @Override
    public ValuatedClaimForm generateClaimForm(String claimUuid) {
        InsuranceClaim claim = insuranceClaimService.getByUuid(claimUuid);
        ValuatedClaimForm form = new ValuatedClaimForm(claim);
        List<InsuranceClaimItem> items = itemDbService.findInsuranceClaimItems(claim.getId());
        form.setClaimItems(buildClaimItems(items));
        List<InsuranceClaimDiagnosis> diagnoses =  diagnosisDbService.findInsuranceClaimDiagnosis(claim.getId());
        form.setClaimDiagnoses(buildDiagnosisComponent(diagnoses));

        return form;
    }

    private List<ValuatedClaimItem> buildClaimItems(List<InsuranceClaimItem> claimItems) {
        return claimItems.stream()
                .map(item -> new ValuatedClaimItem(item))
                .collect(Collectors.toList());
    }

    private List<ValuatedClaimDiagnosis> buildDiagnosisComponent(List<InsuranceClaimDiagnosis> claimDiagnoses) {
        return claimDiagnoses.stream()
                .map(diagnosis -> new ValuatedClaimDiagnosis(diagnosis))
                .collect(Collectors.toList());
    }
}
