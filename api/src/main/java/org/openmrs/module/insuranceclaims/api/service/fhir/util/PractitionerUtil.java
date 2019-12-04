package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Provider;
import org.openmrs.ProviderAttribute;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PROVIDER_EXTERNAL_ID_ATTRIBUTE_UUID;

public final class PractitionerUtil {
    public static Reference buildPractitionerReference(InsuranceClaim claim) {
        Provider provider = claim.getProvider();
        Reference pracitionerReference = FHIRUtils.buildPractitionerReference(claim.getProvider());

        String providerId = provider.getActiveAttributes()
                .stream()
                .filter(c -> c.getAttributeType().getUuid().equals(PROVIDER_EXTERNAL_ID_ATTRIBUTE_UUID))
                .findFirst()
                .map(ProviderAttribute::getValueReference)
                .orElse(provider.getUuid());

        String reference = FHIRConstants.PRACTITIONER + "/" + providerId;

        pracitionerReference.setReference(reference);

        return pracitionerReference;
    }

    private PractitionerUtil() {}
}
