package org.openmrs.module.insuranceclaims.forms;

import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

public interface ClaimFormService {

    InsuranceClaim createClaim(NewClaimForm form);
    Bill createBill(NewClaimForm form);
}
