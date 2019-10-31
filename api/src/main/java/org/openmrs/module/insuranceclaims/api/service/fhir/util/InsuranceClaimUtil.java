package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimDiagnosisService;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.createIdentifier;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.getIdentifierValueByCode;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.SpecialComponentUtil.createSpecialComponent;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.SpecialComponentUtil.getSpecialConditionComponentFromCategory;

public final class InsuranceClaimUtil {
    public static final String MEDICAL_RECORD_NUMBER = "MR";
    public static final String ACCESSION_ID = "ACSN";
    public static final String HL7_VALUESET_SYSTEM = "https://hl7.org/fhir/valueset-identifier-type.html";

    public static List<Claim.DiagnosisComponent> getClaimDiagnosis(InsuranceClaim omrsClaim) {
        List<Claim.DiagnosisComponent> claimDiagnosis = Context
                .getService(FHIRClaimDiagnosisService.class)
                .createClaimDiagnosisComponent(omrsClaim);

        return claimDiagnosis;
    }

    public static Reference buildLocationReference(Location location) {

        StringBuilder display = new StringBuilder();
        display.append(location.getName());
        display.append(", ");
        display.append(location.getTags());

        Reference locationReference = new Reference();
        String uri = FHIRConstants.LOCATION +
                "/" +
                location.getUuid();
        locationReference.setReference(uri);
        locationReference.setDisplay(display.toString());
        locationReference.setId(location.getUuid());

        return locationReference;
    }

    public static CodeableConcept createClaimVisitType(InsuranceClaim omrsClaim) {
        String omrsVisitType = omrsClaim.getVisitType().getName();
        CodeableConcept visitType = new CodeableConcept();
        visitType.setText(omrsVisitType);
        return visitType;
    }

    public static VisitType getClaimVisitType(Claim claim, List<String> errors) {
        String visitTypeName = claim.getType().getText();
        List<VisitType> visitType = getVisitTypeByName(visitTypeName);
        if (visitType.size() > 1) {
            errors.add("More than one matching visit type was found: %");
            return null;
        }
        return visitType.get(0);
    }
    public static Patient getClaimPatient(Claim claim, List<String> errors) {
        String patientUuid = FHIRUtils.getObjectUuidByReference(claim.getPatient());
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        if (patient == null) {
            errors.add("Could not find patient");
            return null;
        }
        return patient;
    }

    public static Provider getClaimProvider(Claim claim, List<String> errors) {
        String providerUUid = FHIRUtils.getObjectUuidByReference(claim.getProvider());
        Provider provider = Context.getProviderService().getProviderByUuid(providerUUid);
        if (provider == null) {
            errors.add("Could not find provider");
            return null;
        }
        return provider;
    }

    public static Location getClaimLocation(Claim claim, List<String> errors) {
        String locationUuid = FHIRUtils.getObjectUuidByReference(claim.getFacility());
        Location location = Context.getLocationService().getLocationByUuid(locationUuid);
        if (location == null) {
            errors.add("Could not find provider");
            return null;
        }
        return location;
    }

    public static List<Identifier> createClaimIdentifier(InsuranceClaim omrsClaim) {
        List<Identifier> fhirIdentifier = new LinkedList<>();
        fhirIdentifier.add(createUuidIdentifier(omrsClaim));
        fhirIdentifier.add(createClaimCodeIdentifier(omrsClaim));

        return fhirIdentifier;
    }

    public static Map<String, Date> getClaimBillablePeriod(Claim claim, List<String> errors) {
        Map<String, Date> claimPeriod = new HashMap<>();
        Period billablePeriod = claim.getBillablePeriod();
        Date from = billablePeriod.getStart();
        if (from == null) {
            errors.add("Date 'from' is missing");
        }
        Date to = billablePeriod.getEnd();
        if (to == null) {
            errors.add("Date 'to' is missing");
        }
        claimPeriod.put("from", from);
        claimPeriod.put("to", to);

        return claimPeriod;
    }

    public static String getClaimExplanation(Claim claim, List<String> errors) {
        try {
            return getSpecialConditionComponentFromCategory(claim, "explanation");
        } catch (FHIRException e) {
            errors.add(e.getMessage());
            return null;
        }
    }

    public static String getClaimGuaranteeId(Claim claim, List<String> errors) {
        try {
            return getSpecialConditionComponentFromCategory(claim, "guarantee_id");
        } catch (FHIRException e) {
            errors.add(e.getMessage());
            return null;
        }
    }

    public static String getClaimCode(Claim claim, List<String> errors) {
        return getIdentifierValueByCode(claim, MEDICAL_RECORD_NUMBER, errors);
    }

    public static String getClaimUuid(Claim claim, List<String> errors) {
        return getIdentifierValueByCode(claim, ACCESSION_ID, errors);
    }

    public static Date getClaimDateCreated(Claim claim, List<String> errors) {
        Date created = claim.getCreated();
        if (created == null) {
            errors.add("Date 'created' is missing");
            return null;
        }
        return created;
    }

    public static Claim.SpecialConditionComponent createClaimExplanationInformation(InsuranceClaim omrsClaim) {
        return createSpecialComponent(omrsClaim.getExplanation(), "explanation");
    }

    public static Claim.SpecialConditionComponent createClaimGuaranteeIdInformation(InsuranceClaim omrsClaim) {
        return createSpecialComponent(omrsClaim.getGuaranteeId(), "guarantee_id");
    }

    private static Identifier createClaimCodeIdentifier(InsuranceClaim omrsClaim) {
        return createIdentifier(omrsClaim.getClaimCode(), MEDICAL_RECORD_NUMBER, HL7_VALUESET_SYSTEM);
    }

    private static Identifier createUuidIdentifier(InsuranceClaim omrsClaim) {
        return createIdentifier(omrsClaim.getUuid(), ACCESSION_ID, HL7_VALUESET_SYSTEM);
    }

    private static List<VisitType> getVisitTypeByName(String visitTypeName) {
        return Context.getVisitService().getVisitTypes(visitTypeName);
    }

}
