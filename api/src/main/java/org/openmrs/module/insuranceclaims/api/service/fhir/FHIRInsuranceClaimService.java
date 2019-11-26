package org.openmrs.module.insuranceclaims.api.service.fhir;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.db.AttributeService;

import java.util.List;

public interface FHIRInsuranceClaimService {
    Claim generateClaim(InsuranceClaim omrsClaim) throws FHIRException;

    InsuranceClaim generateOmrsClaim(Claim claim, List<String> errors);

    void setAttributeService(AttributeService attributeService);
}
