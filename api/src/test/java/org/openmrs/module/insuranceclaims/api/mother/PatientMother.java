package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;

import java.util.UUID;

public final class PatientMother {

	/**
	 * Creates the Patient's test instance
	 *
	 * @return - the Patient instance
	 */
	public static Patient createTestInstance() {
		Location location = Context.getLocationService().getLocation(1);
		return createTestInstance(location);
	}

	/**
	 * Creates the Patient's test instance with the specific location object
	 *
	 * @param location - the related patient location
	 * @return - the Patient instance
	 */
	public static Patient createTestInstance(Location location) {
		PersonName name = new PersonName();
		name.setGivenName("some given name");
		name.setFamilyName("some family name");
		Patient patient = new Patient();
		patient.addName(name);
		patient.setGender("M");
		PatientIdentifier identifier = new PatientIdentifier();
		identifier.setIdentifier(UUID.randomUUID().toString());
		identifier.setLocation(location);
		identifier.setIdentifierType(Context.getPatientService().getPatientIdentifierType(2));
		patient.addIdentifier(identifier);
		return patient;
	}

	private PatientMother() {
	}
}
