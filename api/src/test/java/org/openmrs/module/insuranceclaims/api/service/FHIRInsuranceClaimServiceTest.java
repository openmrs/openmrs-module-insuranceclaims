package org.openmrs.module.insuranceclaims.api.service;

import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimDiagnosisMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimItemMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRInsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PATIENT_EXTERNAL_ID_IDENTIFIER_UUID;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.EXTERNAL_ID_DATASET_PATH;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.INSURANCE_CLAIM_TEST_DIAGNOSIS_DATASET;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.INSURANCE_CLAIM_TEST_ITEM_CONCEPT_DATASET;

public class FHIRInsuranceClaimServiceTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private InsuranceClaimDao insuranceClaimDao;

    @Autowired
    private InsuranceClaimDiagnosisDao insuranceClaimDiagnosisDao;

    @Autowired
    private InsuranceClaimItemDao insuranceClaimItemDao;

    @Autowired
    private FHIRInsuranceClaimService insuranceClaimService;

    private InsuranceClaim insuranceClaim;

    @Before
    public void setUp() throws Exception {
        executeDataSet(EXTERNAL_ID_DATASET_PATH);

        insuranceClaim = createTestInstance();

        InsuranceClaim insuranceClaim = createTestInstance();
        insuranceClaimDao.saveOrUpdate(insuranceClaim);

        InsuranceClaimDiagnosis insuranceClaimDiagnosis = createTestClaimDiagnosis();
        insuranceClaimDiagnosisDao.saveOrUpdate(insuranceClaimDiagnosis);

        InsuranceClaimItem insuranceClaimItem = createTestClaimItem();
        insuranceClaimItemDao.saveOrUpdate(insuranceClaimItem);
    }

    @Test
    public void generateFhirClaim_shouldMapInsuranceClaimToFhirClaim() throws FHIRException {
        InsuranceClaim savedInsuranceClaim = insuranceClaimDao.getByUuid(insuranceClaim.getUuid());
        Claim generatedClaim = insuranceClaimService.generateClaim(savedInsuranceClaim);

        Assert.assertThat(generatedClaim, Matchers.notNullValue());
        Assert.assertThat(generatedClaim.getId(), Matchers.equalTo(insuranceClaim.getClaimCode()));
        Assert.assertThat(generatedClaim.getBillablePeriod().getStart(), Matchers.equalTo(insuranceClaim.getDateFrom()));
        Assert.assertThat(generatedClaim.getBillablePeriod().getEnd(), Matchers.equalTo(insuranceClaim.getDateTo()));
        Assert.assertThat(generatedClaim.getFacility().getReference(), Matchers.equalTo(getExpectedLocationReference()));
        Assert.assertThat(generatedClaim.getPatient().getReference(), Matchers.equalTo(getExpectedPatientReference()));
        Assert.assertThat(generatedClaim.getProvider().getReference(), Matchers.equalTo(getExpectedPractitionerReference()));
        Assert.assertThat(generatedClaim.getIdentifier(), Matchers.hasSize(2));
        Assert.assertThat(generatedClaim.getIdentifier()
                .stream()
                .map(Identifier::getValue)
                .collect(Collectors.toList()), Matchers.contains(getExpectedIdentifierCodes().toArray()));
        Assert.assertThat(generatedClaim.getTotal().getValue(), Matchers.equalTo(getExpectedTotal()));

        Assert.assertThat(generatedClaim.getDiagnosis(), Matchers.hasSize(1));

        Assert.assertThat(generatedClaim.getItem(), Matchers.hasSize(1));

        String diagnosisName = generatedClaim.getDiagnosis().get(0).getDiagnosisCodeableConcept().getText();
        Assert.assertThat(diagnosisName, Matchers.equalTo("Malaria, confirmed"));
        //TODO: Add item and information after changes in database
    }

    @Test
    public void generateOmrsClaim_shouldMapFhirClaimToOmrsClaim() throws FHIRException {
        InsuranceClaim savedInsuranceClaim = insuranceClaimDao.getByUuid(insuranceClaim.getUuid());

        Claim fhirClaim = insuranceClaimService.generateClaim(savedInsuranceClaim);
        List<String> errors = new ArrayList<>();
        InsuranceClaim generatedClaim = insuranceClaimService.generateOmrsClaim(fhirClaim, errors);

        Assert.assertThat(errors, Matchers.hasSize(0));
        Assert.assertThat(generatedClaim.getUuid(), Matchers.equalTo(savedInsuranceClaim.getUuid()));
        Assert.assertThat(generatedClaim.getPatient(), Matchers.equalTo(savedInsuranceClaim.getPatient()));
        Assert.assertThat(generatedClaim.getProvider(), Matchers.equalTo(savedInsuranceClaim.getProvider()));
        Assert.assertThat(generatedClaim.getClaimCode(), Matchers.equalTo(savedInsuranceClaim.getClaimCode()));
        Assert.assertThat(generatedClaim.getLocation(), Matchers.equalTo(savedInsuranceClaim.getLocation()));
        Assert.assertThat(generatedClaim.getVisitType(), Matchers.equalTo(savedInsuranceClaim.getVisitType()));
        Assert.assertThat(generatedClaim.getDateFrom(), Matchers.equalTo(savedInsuranceClaim.getDateFrom()));
        Assert.assertThat(generatedClaim.getDateTo(), Matchers.equalTo(savedInsuranceClaim.getDateTo()));
        Assert.assertThat(generatedClaim.getDateCreated(), Matchers.equalTo(savedInsuranceClaim.getDateCreated()));
        Assert.assertThat(generatedClaim.getGuaranteeId(), Matchers.equalTo(savedInsuranceClaim.getGuaranteeId()));
        Assert.assertThat(generatedClaim.getExplanation(), Matchers.equalTo(savedInsuranceClaim.getExplanation()));
        //TODO: Add item and information after changes in database

    }

    private InsuranceClaim createTestInstance() {
        Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
        Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
        VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierTypeByUuid(PATIENT_EXTERNAL_ID_IDENTIFIER_UUID);
        return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
    }

    private InsuranceClaimDiagnosis createTestClaimDiagnosis() throws Exception {
        executeDataSet(INSURANCE_CLAIM_TEST_DIAGNOSIS_DATASET);
        Concept concept = Context.getConceptService().getConceptByUuid("160148AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return InsuranceClaimDiagnosisMother.createTestInstance(concept, this.insuranceClaim);
    }

    private InsuranceClaimItem createTestClaimItem() throws Exception {
        executeDataSet(INSURANCE_CLAIM_TEST_ITEM_CONCEPT_DATASET);
        Concept concept = Context.getConceptService().getConceptByUuid("160148BAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        InsuranceClaimItem testItem = InsuranceClaimItemMother.createTestInstance(concept, this.insuranceClaim);
        testItem.getItem().setItem(concept);
        return testItem;
    }

    private String getExpectedLocationReference() {
        return "Location/" + insuranceClaim.getLocation().getActiveAttributes().iterator().next().getValueReference();
    }

    private String getExpectedPatientReference() {
        return "Patient/" + insuranceClaim.getPatient().getActiveIdentifiers().get(0).getIdentifier();
    }

    private String getExpectedPractitionerReference() {
        return "Practitioner/" + insuranceClaim.getProvider().getActiveAttributes().iterator().next().getValueReference();
    }

    private BigDecimal getExpectedTotal() {
        return insuranceClaim.getClaimedTotal();
    }

    private List<String> getExpectedIdentifierCodes() {
        List<String> expectedCodes = new ArrayList<>();
        expectedCodes.add(insuranceClaim.getUuid());
        expectedCodes.add(insuranceClaim.getClaimCode());
        return expectedCodes;
    }
}

