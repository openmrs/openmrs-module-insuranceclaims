package org.openmrs.module.insuranceclaims.api.service.fhir.impl;


import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Money;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.module.fhir.api.util.BaseOpenMRSDataUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRInsuranceClaimService;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.buildLocationReference;
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
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimLocation;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimPatient;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimProvider;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimUuid;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil.getClaimVisitType;

public class FHIRInsuranceClaimServiceImpl implements FHIRInsuranceClaimService {

    public Claim generateClaim(InsuranceClaim omrsClaim) {
        Claim claim = new Claim();

        BaseOpenMRSDataUtil.setBaseExtensionFields(claim, omrsClaim);

        //Set Claim id to fhir Claim
        IdType claimId = new IdType();
        claimId.setValue(omrsClaim.getClaimCode());
        claim.setId(claimId);

        //Set provider
        Reference providerReference = FHIRUtils.buildPractitionerReference(omrsClaim.getProvider());

        claim.setProvider(providerReference);
        //Set enterer
        claim.setEnterer(providerReference);

        //Set patient
        Reference patientReference = FHIRUtils.buildPatientOrPersonResourceReference(omrsClaim.getPatient());
        claim.setPatient(patientReference);

        //Set facility
        Reference locationReference = buildLocationReference(omrsClaim.getLocation());
        claim.setFacility(locationReference);

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
        //TODO: Add after item model update

        //Set type
        claim.setType(createClaimVisitType(omrsClaim));

        //Set diagnosis
        claim.setDiagnosis(getClaimDiagnosis(omrsClaim));

        return claim;
    }

    public InsuranceClaim generateOmrsClaim(Claim claim, List<String> errors) {
        InsuranceClaim omrsClaim = new InsuranceClaim();

        BaseOpenMRSDataUtil.readBaseExtensionFields(omrsClaim, claim);
        BaseOpenMRSDataUtil.setBaseExtensionFields(claim, omrsClaim);

        omrsClaim.setUuid(getClaimUuid(claim, errors));

        //Set provider
        omrsClaim.setProvider(getClaimProvider(claim, errors));

        //Set patient
        omrsClaim.setPatient(getClaimPatient(claim, errors));

        //Set facility
        omrsClaim.setLocation(getClaimLocation(claim, errors));

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

        //Set guarnateeId
        omrsClaim.setGuaranteeId(getClaimGuaranteeId(claim, errors));

        //Set type
        omrsClaim.setVisitType(getClaimVisitType(claim, errors));

        //Set items
        //TODO: Add after item model update

        return omrsClaim;
    }
}

