package org.openmrs.module.insuranceclaims.api.service.forms;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.NewClaimForm;

public interface ClaimFormService {

    InsuranceClaim createClaim(NewClaimForm form);
}
