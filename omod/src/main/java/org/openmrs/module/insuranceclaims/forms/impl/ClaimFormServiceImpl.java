package org.openmrs.module.insuranceclaims.forms.impl;


import ca.uhn.fhir.util.DateUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.model.PaymentType;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.service.BillService;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimDiagnosisService;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimItemService;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.service.ProvidedItemService;
import org.openmrs.module.insuranceclaims.forms.ClaimFormService;
import org.openmrs.module.insuranceclaims.forms.NewClaimForm;
import org.openmrs.module.insuranceclaims.forms.ProvidedItemInForm;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClaimFormServiceImpl implements ClaimFormService {

    private BillService billService;

    private ProvidedItemService providedItemService;

    private InsuranceClaimService insuranceClaimService;

    private InsuranceClaimItemService insuranceClaimItemService;

    private InsuranceClaimDiagnosisService insuranceClaimDiagnosisService;

    private static final int SINGLE_ITEM = 1;

    private static final String[] FORM_DATE_FORMAT = {"yyy-mm-dd"};

    private static final String INVALID_LOCATION_ERROR = "You must select valid location";

    @Override
    @Transactional
    public InsuranceClaim createClaim(NewClaimForm form) {
        InsuranceClaim nextClaim = new InsuranceClaim();

        nextClaim.setAdjustment(form.getClaimJustification());
        nextClaim.setExplanation(form.getClaimExplanation());

        VisitType visitType = Context.getVisitService().getVisitTypeByUuid(form.getVisitType());
        nextClaim.setVisitType(visitType);

        Patient patient = Context.getPatientService().getPatientByUuid(form.getPatient());
        nextClaim.setPatient(patient);

        nextClaim.setGuaranteeId(form.getGuaranteeId());
        nextClaim.setClaimCode(form.getClaimCode());
        nextClaim.setStatus(InsuranceClaimStatus.ENTERED);
        nextClaim.setLocation(getClaimLocation(form));

        assignDatesFromFormToClaim(nextClaim, form);

        List<InsuranceClaimItem> items = generateClaimItems(form.getProvidedItems());
        List<ProvidedItem> claimProvidedItems = items.stream()
                .map(item -> item.getItem())
                .collect(Collectors.toList());

        createClaimBill(nextClaim, claimProvidedItems);
        nextClaim.getBill().setPaymentType(PaymentType.INSURANCE_CLAIM);
        insuranceClaimService.saveOrUpdate(nextClaim);

        List<InsuranceClaimDiagnosis> diagnoses = generateClaimDiagnoses(form.getDiagnoses(), nextClaim);
        diagnoses.stream().forEach(diagnosis -> insuranceClaimDiagnosisService.saveOrUpdate(diagnosis));

        items.stream().forEach(item -> {
            item.setClaim(nextClaim);
            insuranceClaimItemService.saveOrUpdate(item);
        });

        return nextClaim;
    }

    @Override
    @Transactional
    public Bill createBill(NewClaimForm form) {
        List<InsuranceClaimItem> items = generateClaimItems(form.getProvidedItems());
        List<ProvidedItem> claimProvidedItems = items.stream()
                .map(item -> item.getItem())
                .collect(Collectors.toList());

        Bill bill = billService.generateBill(claimProvidedItems);
        bill.setPaymentType(PaymentType.CASH);
        billService.saveOrUpdate(bill);

        return bill;
    }

    public void setBillService(BillService billService) {
        this.billService = billService;
    }

    public void setProvidedItemService(ProvidedItemService providedItemService) {
        this.providedItemService = providedItemService;
    }

    public void setInsuranceClaimService(InsuranceClaimService insuranceClaimService) {
        this.insuranceClaimService = insuranceClaimService;
    }

    public void setInsuranceClaimItemService(InsuranceClaimItemService insuranceClaimItemService) {
        this.insuranceClaimItemService = insuranceClaimItemService;
    }

    public void setInsuranceClaimDiagnosisService(InsuranceClaimDiagnosisService insuranceClaimDiagnosisService) {
        this.insuranceClaimDiagnosisService = insuranceClaimDiagnosisService;
    }

    private List<InsuranceClaimItem> generateClaimItems(Map<String, ProvidedItemInForm> allProvidedItems) {
        List<InsuranceClaimItem> consumptions = new ArrayList<>();

        for (ProvidedItemInForm itemConsumptions:  allProvidedItems.values()) {
            List<InsuranceClaimItem> items = getConsumedItemsOfType(itemConsumptions);
            consumptions.addAll(items);
        }

        return consumptions;
    }

    private List<InsuranceClaimItem> getConsumedItemsOfType(ProvidedItemInForm formItems) {
        List<InsuranceClaimItem> items = new ArrayList<>();
        String explanation = formItems.getExplanation();
        String justification = formItems.getJustification();

        for (String nextItemUuid : formItems.getItems()) {
            ProvidedItem provideditem = providedItemService.getByUuid(nextItemUuid);
            InsuranceClaimItem nextInsuranceClaimItem = new InsuranceClaimItem();
            nextInsuranceClaimItem.setItem(provideditem);
            nextInsuranceClaimItem.setQuantityProvided(provideditem.getNumberOfConsumptions());
            nextInsuranceClaimItem.setJustification(justification);
            nextInsuranceClaimItem.setExplanation(explanation);
            items.add(nextInsuranceClaimItem);
        }
        return items;
    }

    private List<InsuranceClaimDiagnosis> generateClaimDiagnoses(List<String> diagnosesUuidList, InsuranceClaim claim) {
        List<InsuranceClaimDiagnosis> diagnoses = new ArrayList<>();

        for (String uuid: diagnosesUuidList) {
            Concept diagnosisConcept = Context.getConceptService().getConceptByUuid(uuid);
            InsuranceClaimDiagnosis nextDiagnosis = new InsuranceClaimDiagnosis(diagnosisConcept, claim);
            diagnoses.add(nextDiagnosis);
        }
        return diagnoses;
    }

    private void assignDatesFromFormToClaim(InsuranceClaim claim, NewClaimForm form) {
        Date startDate = DateUtils.parseDate(form.getStartDate(), FORM_DATE_FORMAT);
        Date endDate = DateUtils.parseDate(form.getEndDate(), FORM_DATE_FORMAT);
        claim.setDateFrom(startDate);
        claim.setDateTo(endDate);
        claim.setProvider(Context.getProviderService().getProviderByUuid(form.getProvider()));
    }

    private void createClaimBill(InsuranceClaim claim, List<ProvidedItem> claimProvidedItems) {
        Bill bill = billService.generateBill(claimProvidedItems);
        claim.setBill(bill);
        claim.setClaimedTotal(claim.getBill().getTotalAmount());
    }

    private Location getClaimLocation(NewClaimForm form) throws HttpServerErrorException {
        try {
            return Context.getLocationService().getLocation(Integer.parseInt(form.getLocation()));
        } catch (NumberFormatException exception) {
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, INVALID_LOCATION_ERROR);
        }
    }
}
