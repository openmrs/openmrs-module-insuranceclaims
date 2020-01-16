package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Money;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.BaseOpenMRSDataUtil;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.db.AttributeService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimDiagnosisService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimItemService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRInsuranceClaimService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.getIdFromReference;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.getUnambiguousElement;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PERIOD_FROM;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PERIOD_TO;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.createClaimExplanationInformation;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.createClaimGuaranteeIdInformation;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.createClaimIdentifier;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.createClaimVisitType;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimBillablePeriod;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimCode;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimDateCreated;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimExplanation;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimGuaranteeId;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimUuid;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.LocationUtil.buildLocationReference;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.PatientUtil.buildPatientReference;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.PractitionerUtil.buildPractitionerReference;

public class FHIRInsuranceClaimServiceImpl implements FHIRInsuranceClaimService {

    private AttributeService attributeService;

    private FHIRClaimItemService claimItemService;

    private FHIRClaimDiagnosisService claimDiagnosisService;

    @Override
    public Claim generateClaim(InsuranceClaim omrsClaim) throws FHIRException {
        Claim claim = new Claim();
        BaseOpenMRSDataUtil.setBaseExtensionFields(claim, omrsClaim);

        //Set Claim id to fhir Claim
        IdType claimId = new IdType();
        claimId.setValue(omrsClaim.getClaimCode());
        claim.setId(claimId);

        //Set provider
        Reference providerReference = buildPractitionerReference(omrsClaim);
        claim.setProvider(providerReference);
        //Set enterer
        claim.setEnterer(providerReference);

        //Set patient
        claim.setPatient(buildPatientReference(omrsClaim));

        //Set facility
        claim.setFacility(buildLocationReference(omrsClaim));

        //Set identifier
        List<Identifier> identifiers = createClaimIdentifier(omrsClaim);
        claim.setIdentifier(identifiers);

        //Set billablePeriod
        Period billablePeriod = new Period();
        billablePeriod.setStart(omrsClaim.getDateFrom());
        billablePeriod.setEnd(omrsClaim.getDateTo());
        claim.setBillablePeriod(billablePeriod);

        //Set total
        Money total = new Money();
        total.setValue(omrsClaim.getClaimedTotal());
        claim.setTotal(total);

        //Set created
        claim.setCreated(omrsClaim.getDateCreated());

        //Set information
        List<Claim.SpecialConditionComponent> claimInformation = new ArrayList<>();

        claimInformation.add(createClaimGuaranteeIdInformation(omrsClaim));
        claimInformation.add(createClaimExplanationInformation(omrsClaim));

        claim.setInformation(claimInformation);

        //Set type
        claim.setType(createClaimVisitType(omrsClaim));
        //Set items
        claimItemService.assignItemsWithInformationToClaim(claim, omrsClaim);

        //Set diagnosis
        claim.setDiagnosis(claimDiagnosisService.generateClaimDiagnosisComponent(omrsClaim));

        return claim;
    }

    @Override
    public InsuranceClaim generateOmrsClaim(Claim claim, List<String> errors) {
        InsuranceClaim omrsClaim = new InsuranceClaim();

        BaseOpenMRSDataUtil.readBaseExtensionFields(omrsClaim, claim);
        BaseOpenMRSDataUtil.setBaseExtensionFields(claim, omrsClaim);

        omrsClaim.setUuid(getClaimUuid(claim, errors));

        //Set provider
        omrsClaim.setProvider(getClaimProviderByExternalId(claim));

        //Set patient
        omrsClaim.setPatient(getClaimPatientByExternalIdentifier(claim));

        //Set facility
        omrsClaim.setLocation(getClaimLocationByExternalId(claim));

        //Set identifier
        omrsClaim.setClaimCode(getClaimCode(claim, errors));

        //Set billablePeriod
        Map<String, Date> period = getClaimBillablePeriod(claim, errors);
        omrsClaim.setDateFrom(period.get(PERIOD_FROM));
        omrsClaim.setDateTo(period.get(PERIOD_TO));

        //Set total
        Money total = claim.getTotal();
        omrsClaim.setClaimedTotal(total.getValue());

        //Set created
        omrsClaim.setDateCreated(getClaimDateCreated(claim, errors));

        //Set explanation
        omrsClaim.setExplanation(getClaimExplanation(claim, errors));

        //Set guaranteeId
        omrsClaim.setGuaranteeId(getClaimGuaranteeId(claim, errors));

        //Set type
        omrsClaim.setVisitType(getClaimVisitType(claim));

        return omrsClaim;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public void setClaimItemService(FHIRClaimItemService claimItemService) {
        this.claimItemService = claimItemService;
    }

    public void setClaimDiagnosisService(FHIRClaimDiagnosisService claimDiagnosisService) {
        this.claimDiagnosisService = claimDiagnosisService;
    }

    private Provider getClaimProviderByExternalId(Claim claim) {
        String practitionerExternalId = getIdFromReference(claim.getEnterer());
        List<Provider> providersWithExernalId = attributeService.getProviderByExternalIdAttribute(practitionerExternalId);
        return getUnambiguousElement(providersWithExernalId);
    }

    private Location getClaimLocationByExternalId(Claim claim) {
        String locationExternalId = getIdFromReference(claim.getFacility());
        List<Location> locationsWithExternalId = attributeService.getLocationByExternalIdAttribute(locationExternalId);
        return getUnambiguousElement(locationsWithExternalId);
    }

    private Patient getClaimPatientByExternalIdentifier(Claim claim) {
        String patientExternalId = getIdFromReference(claim.getPatient());
        List<Patient> patientsWithExternalId = attributeService.getPatientByExternalIdIdentifier(patientExternalId);
        return getUnambiguousElement(patientsWithExternalId);
    }

    private  static VisitType getClaimVisitType(Claim claim) {
        String visitTypeName = claim.getType().getText();
        List<VisitType> visitType = getVisitTypeByName(visitTypeName);
        return getUnambiguousElement(visitType);
    }

    private static List<VisitType> getVisitTypeByName(String visitTypeName) {
        return Context.getVisitService().getVisitTypes(visitTypeName);
    }

}

