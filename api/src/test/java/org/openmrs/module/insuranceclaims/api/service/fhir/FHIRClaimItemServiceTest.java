package org.openmrs.module.insuranceclaims.api.service.fhir;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimItemMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.service.db.ItemDbService;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.SpecialComponentUtil;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CATEGORY_SERVICE;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ITEM_ADJUDICATION_GENERAL_CATEGORY;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ITEM_ADJUDICATION_REJECTION_REASON_CATEGORY;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.SEQUENCE_FIRST;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimItemUtil.createFhirItemService;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.INSURANCE_CLAIM_TEST_ITEM_CONCEPT_DATASET;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.TEST_SERVICE_CODE;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.TEST_SERVICE_UUID;

public class FHIRClaimItemServiceTest extends BaseModuleContextSensitiveTest {
    private final static Float UNIT_PRICE = 10.10f;
    private final static Boolean IS_SERVICE = false;

    @Autowired
    private FHIRClaimItemService claimItemService;

    @Autowired
    private InsuranceClaimItemDao insuranceClaimItemService;

    @Autowired
    private InsuranceClaimService insuranceClaimService;

    @Autowired
    private ItemDbService itemDbService;

    private InsuranceClaim testInsuranceClaim;

    private InsuranceClaimItem testInsuranceItem;

    @Before
    public void setUp() throws Exception {
        Context.flushSession();
        Context.clearSession();

        this.testInsuranceClaim = createTestClaim();
        this.testInsuranceItem = createTestClaimItem();

        insuranceClaimService.saveOrUpdate(this.testInsuranceClaim);
        insuranceClaimItemService.saveOrUpdate(this.testInsuranceItem);
    }

    @After
    public void tearDown() {
        insuranceClaimService.delete(this.testInsuranceClaim);
    }

    @Test
    public void generateFhirClaimItem_shouldMapInsuranceClaimItemToFhirClaimItem() {
        List<Claim.ItemComponent> generatedClaimItem =
                claimItemService.generateClaimItemComponent(this.testInsuranceClaim);

        Claim.ItemComponent actual = generatedClaimItem.get(0);
        Assert.assertThat(generatedClaimItem, Matchers.hasSize(1));
        Assert.assertThat(actual.getCategory().getText(), Matchers.equalTo(CATEGORY_SERVICE));
        Assert.assertThat(actual.getQuantity().getValue().intValue(),
                Matchers.equalTo(testInsuranceItem.getQuantityProvided()));
        Assert.assertThat(actual.getService().getText(), Matchers.equalTo(getExpectedCode()));
        Assert.assertThat(actual.getUnitPrice().getValue().floatValue(), Matchers.equalTo(21000.00f));
    }

    @Test
    public void generateOmrsClaimItem_shouldMapFhirClaimItemToInsuranceClaimItem() throws FHIRException {
        Claim claim = createTestFhirClaimWithItems();
        List<String> errors = new ArrayList<>();
        List<InsuranceClaimItem> generatedClaimItem = claimItemService.generateOmrsClaimItems(claim, errors);
        InsuranceClaimItem actual = generatedClaimItem.get(0);

        Assert.assertThat(generatedClaimItem, Matchers.hasSize(1));
        Assert.assertThat(actual.getItem().getItem(), Matchers.equalTo(testInsuranceItem.getItem().getItem()));
        Assert.assertThat(actual.getQuantityProvided(), Matchers.equalTo(testInsuranceItem.getQuantityProvided()));
        Assert.assertThat(actual.getExplanation(),
                Matchers.equalTo(SpecialComponentUtil.getSpecialConditionComponentBySequenceNumber(claim, 3)));
    }

    @Test
    public void generateFhirClaimResponseItem_shouldMapInsuranceClaimItemToFhirClaimResponse() {
        List<ClaimResponse.ItemComponent> generatedClaimResponseItem =
                claimItemService.generateClaimResponseItemComponent(this.testInsuranceClaim);

        ClaimResponse.ItemComponent actual = generatedClaimResponseItem.get(0);
        ClaimResponse.AdjudicationComponent generalAdjudication = actual.getAdjudication().get(0);
        ClaimResponse.AdjudicationComponent rejectAdjudication = actual.getAdjudication().get(1);

        Assert.assertThat(generatedClaimResponseItem, Matchers.hasSize(1));
        Assert.assertThat(actual.getAdjudication(), Matchers.hasSize(2));
        Assert.assertThat(actual.getSequenceLinkId(), Matchers.equalTo(SEQUENCE_FIRST));
        Assert.assertThat(generalAdjudication.getValue().intValue(), Matchers.equalTo(testInsuranceItem.getQuantityApproved()));
        Assert.assertThat(generalAdjudication.getAmount().getValue(), Matchers.equalTo(testInsuranceItem.getPriceApproved()));
        Assert.assertThat(generalAdjudication.getCategory().getText(), Matchers.equalTo(ITEM_ADJUDICATION_GENERAL_CATEGORY));
        Assert.assertThat(rejectAdjudication.getCategory().getText(),
                Matchers.equalTo(ITEM_ADJUDICATION_REJECTION_REASON_CATEGORY));
        Assert.assertThat(rejectAdjudication.getReason().getCodingFirstRep().getCode(),
                Matchers.equalTo(testInsuranceItem.getRejectionReason()));
    }

    @Test
    public void generateOmrsClaimItem_shouldMapFhirClaimResponseItemToInsuranceClaimItem() throws FHIRException {
        ClaimResponse claim = createTestFhirClaimResponseWithItems();
        claim.setProcessNote(claimItemService.generateClaimResponseNotes(testInsuranceClaim));
        List<String> errors = new ArrayList<>();
        List<InsuranceClaimItem> generatedClaimItem = claimItemService.generateOmrsClaimResponseItems(claim, errors);
        InsuranceClaimItem actual = generatedClaimItem.get(0);

        Assert.assertThat(generatedClaimItem, Matchers.hasSize(1));
        Assert.assertThat(actual.getItem().getItem(), Matchers.equalTo(testInsuranceItem.getItem().getItem()));
        Assert.assertThat(actual.getQuantityApproved(), Matchers.equalTo(testInsuranceItem.getQuantityApproved()));
        Assert.assertThat(actual.getRejectionReason(), Matchers.equalTo(testInsuranceItem.getRejectionReason()));
        Assert.assertThat(actual.getPriceApproved(), Matchers.equalTo(testInsuranceItem.getPriceApproved()));
        Assert.assertThat(actual.getStatus(), Matchers.equalTo(testInsuranceItem.getStatus()));
        Assert.assertThat(actual.getJustification(), Matchers.equalTo(testInsuranceItem.getJustification()));
    }

    @Test
    public void generateClaimResponseNotes_shouldCreateProcessNotesForItemsWithCorrectNumering() throws FHIRException {
        List<ClaimResponse.NoteComponent> generatedNotes = claimItemService.generateClaimResponseNotes(this.testInsuranceClaim);
        ClaimResponse.NoteComponent actualNote = generatedNotes.get(0);

        Assert.assertThat(generatedNotes, Matchers.hasSize(1));
        Assert.assertThat(actualNote.getNumber(), Matchers.equalTo(1));
        Assert.assertThat(actualNote.getText(), Matchers.equalTo(testInsuranceItem.getJustification()));
    }

    private InsuranceClaim createTestClaim() {
        Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
        Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
        VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);

        return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
    }

    private InsuranceClaimItem createTestClaimItem() throws Exception {
        executeDataSet(INSURANCE_CLAIM_TEST_ITEM_CONCEPT_DATASET);
        Concept concept = Context.getConceptService().getConceptByUuid(TEST_SERVICE_UUID);
        InsuranceClaimItem testItem = InsuranceClaimItemMother.createTestInstance(concept, this.testInsuranceClaim);
        testItem.getItem().setItem(concept);
        return testItem;
    }

    private Claim createTestFhirClaimWithItems() {
        Claim claim = new Claim();
        List<Claim.ItemComponent> fhirItems = claimItemService.generateClaimItemComponent(this.testInsuranceClaim);

        claim.setItem(fhirItems);
        claim.setInformation(createClaimTestInformation());
        return claim;
    }

    private ClaimResponse createTestFhirClaimResponseWithItems() {
        ClaimResponse claimResponse = new ClaimResponse();
        List<ClaimResponse.ItemComponent> fhirItems = claimItemService.generateClaimResponseItemComponent(this.testInsuranceClaim);
        List<InsuranceClaimItem> items = itemDbService.findInsuranceClaimItems(this.testInsuranceClaim.getId());
        List<ClaimResponse.AddedItemComponent> itemCodes = createTestAdditemComponent(items);
        claimResponse.setItem(fhirItems);
        claimResponse.setAddItem(itemCodes);
        return claimResponse;
    }

    private List<ClaimResponse.AddedItemComponent> createTestAdditemComponent(List<InsuranceClaimItem> items) {
        int sequence = 1;
        List<ClaimResponse.AddedItemComponent> addedItems = new ArrayList<>();

        for (InsuranceClaimItem item: items) {
            addedItems.add(createAddItem(item, sequence++));
        }
        return addedItems;
    }

    private ClaimResponse.AddedItemComponent createAddItem(InsuranceClaimItem item, int sequence) {
        ClaimResponse.AddedItemComponent addedItem = new ClaimResponse.AddedItemComponent();
        addedItem.setService(createFhirItemService(item));
        addedItem.addSequenceLinkId(sequence);
        return addedItem;
    }

    private String getExpectedCode() {
        Concept concept = Context.getConceptService().getConceptByUuid(TEST_SERVICE_UUID);
        return concept.getConceptMappings().stream()
                .map(c -> c.getConceptReferenceTerm().toString())
                .filter(s -> s.equals(TEST_SERVICE_CODE))
                .findFirst()
                .orElseThrow(() -> new InvalidGeneratorSetupException("Concept was not correctly exported from dataset"));
    }

    private List<Claim.SpecialConditionComponent> createClaimTestInformation() {
        List<Claim.SpecialConditionComponent> information = new ArrayList<>();
        information.add(SpecialComponentUtil.createSpecialComponent("guaranatee_id", "guarantee"));
        information.add(SpecialComponentUtil.createSpecialComponent("explanation", "explanation_id"));
        information.add(SpecialComponentUtil
                .createSpecialComponent("This value should be assigned to item explanation", "explanation"));
        return information;
    }
}
