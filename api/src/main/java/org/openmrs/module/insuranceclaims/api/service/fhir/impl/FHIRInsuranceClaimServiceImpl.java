package org.openmrs.module.insuranceclaims.api.service.fhir.impl;


import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Money;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.module.fhir.api.util.BaseOpenMRSDataUtil;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.db.AttributeService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRInsuranceClaimService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.getIdFromReference;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.buildLocationReference;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.buildPatientReference;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.buildPractitionerReference;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.createClaimExplanationInformation;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.createClaimGuaranteeIdInformation;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.createClaimIdentifier;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.createClaimVisitType;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimBillablePeriod;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimCode;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimDateCreated;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimDiagnosis;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimExplanation;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimGuaranteeId;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimUuid;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimVisitType;

public class FHIRInsuranceClaimServiceImpl implements FHIRInsuranceClaimService {

    @Autowired
    private AttributeService attributeService;

    public Claim generateClaim(InsuranceClaim omrsClaim) {
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
        List<Claim.SpecialConditionComponent> claimInformation = new LinkedList<>();

        claimInformation.add(createClaimGuaranteeIdInformation(omrsClaim));
        claimInformation.add(createClaimExplanationInformation(omrsClaim));

        claim.setInformation(claimInformation);
        //Set items
        claim.setItem(new ArrayList<>());
        //TODO: Add after item model update

        //Set type
        claim.setType(createClaimVisitType(omrsClaim));

        //Set diagnosis
        try {
            claim.setDiagnosis(getClaimDiagnosis(omrsClaim));
        } catch (NullPointerException nlp) {
            //TODO: Change this
            System.out.println("Diagnosis not found");
        }
        return claim;
    }

    public InsuranceClaim generateOmrsClaim(Claim claim, List<String> errors) {
        InsuranceClaim omrsClaim = new InsuranceClaim();

        BaseOpenMRSDataUtil.readBaseExtensionFields(omrsClaim, claim);
        BaseOpenMRSDataUtil.setBaseExtensionFields(claim, omrsClaim);

        try {
            omrsClaim.setUuid(getClaimUuid(claim, errors));
        } catch (NullPointerException e) {
            errors.add("No uuid found");
        }
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
        omrsClaim.setDateFrom(period.get("from"));
        omrsClaim.setDateTo(period.get("to"));

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
        omrsClaim.setVisitType(getClaimVisitType(claim, errors));

        //Set items
        //TODO: Add after item model update

        return omrsClaim;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    private Provider getClaimProviderByExternalId(Claim claim) {
        String practitionerExternalId = getIdFromReference(claim.getEnterer());
        List<Provider> p = attributeService.getProviderByExternalIdAttribute(practitionerExternalId);
        return getUniqueElement(p, practitionerExternalId);
    }

    private Location getClaimLocationByExternalId(Claim claim) {
        String locationExternalId = getIdFromReference(claim.getFacility());
        List<Location> p = attributeService.getLocationByExternalIdAttribute(locationExternalId);
        return getUniqueElement(p, locationExternalId);
    }

    private Patient getClaimPatientByExternalIdentifier(Claim claim) {
        String patientExternalId = getIdFromReference(claim.getPatient());
        List<Patient> p = attributeService.getPatientByExternalIdIdentifier(patientExternalId);
        return getUniqueElement(p, patientExternalId);
    }

    private <T> T getUniqueElement(List<T> p, String identifier) {
        if (p.isEmpty()) {
            return null;
        }
        Set<T> set = new HashSet<>(p);
        if (set.size() == 1) {
            return p.get(0);
        } else {
            throw new IdentifierNotUniqueException("Could not find unique element of with identifier "
            + identifier + ", elements with identifier: " + p.toString());
        }
    }
}

