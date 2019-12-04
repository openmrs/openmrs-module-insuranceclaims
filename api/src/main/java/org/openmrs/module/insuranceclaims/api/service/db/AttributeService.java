package org.openmrs.module.insuranceclaims.api.service.db;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;

import java.util.List;

public interface AttributeService {

    List<Provider> getProviderByExternalIdAttribute(String extetnalId);

    List<Location> getLocationByExternalIdAttribute(String extetnalId);

    List<Patient> getPatientByExternalIdIdentifier(String externalId);
}
