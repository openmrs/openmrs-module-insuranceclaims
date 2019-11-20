package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.LOCATION_EXTERNAL_ID_ATTRIBUTE_UUID;

public final class LocationUtil {

    public static Reference buildLocationReference(InsuranceClaim claim) {
        Location location = claim.getLocation();
        Reference locationReference = new Reference();
        String referenceId = location.getActiveAttributes()
                .stream()
                .filter(c -> c.getAttributeType().getUuid().equals(LOCATION_EXTERNAL_ID_ATTRIBUTE_UUID))
                .findFirst()
                .map(LocationAttribute::getValueReference)
                .orElse(location.getUuid());

        String reference = FHIRConstants.LOCATION + "/" + referenceId;
        locationReference.setReference(reference);

        String display = location.getName() + ", " + location.getTags();
        locationReference.setDisplay(display);
        locationReference.setId(location.getUuid());

        return locationReference;
    }

    private LocationUtil() {}
}
