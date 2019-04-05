package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;

import java.util.UUID;

public final class PatientMother {

	/**
	 * Creates the Patient's test instance with the specific location object
	 *
	 * @param location - the related patient location
	 * @param identifierType - related identifier type object
	 * @return - the Patient instance
	 */
	public static Patient createTestInstance(Location location, PatientIdentifierType identifierType) {
		PersonName name = new PersonName();
		name.setGivenName("some given name");
		name.setFamilyName("some family name");
		Patient patient = new Patient();
		patient.addName(name);
		patient.setGender("M");
		PatientIdentifier identifier = new PatientIdentifier();
		identifier.setIdentifier(UUID.randomUUID().toString());
		identifier.setLocation(location);
		identifier.setIdentifierType(identifierType);
		patient.addIdentifier(identifier);
		return patient;
	}

	private PatientMother() {
	}
}
