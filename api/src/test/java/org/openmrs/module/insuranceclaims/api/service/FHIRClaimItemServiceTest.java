package org.openmrs.module.insuranceclaims.api.service;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Claim;
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
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDao;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimItemMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimItemService;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.SpecialComponentUtil;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_UUID;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.INSURANCE_CLAIM_TEST_ITEM_CONCEPT_DATASET;

public class FHIRClaimItemServiceTest extends BaseModuleContextSensitiveTest {
    private final static Float UNIT_PRICE = 10.10f;
    private final static Boolean IS_SERVICE = false;

    @Autowired
    private FHIRClaimItemService claimItemService;

    @Autowired
    private InsuranceClaimItemDao itemDao;

    @Autowired
    private InsuranceClaimDao claimDao;

    private InsuranceClaim testInsuranceClaim;

    private InsuranceClaimItem testInsuranceItem;

    @Before
    public void setUp() throws Exception {
        Context.flushSession();
        Context.clearSession();

        this.testInsuranceClaim = createTestClaim();
        this.testInsuranceItem = createTestClaimItem();

        claimDao.saveOrUpdate(this.testInsuranceClaim);
        itemDao.saveOrUpdate(this.testInsuranceItem);
    }

    @After
    public void tearDown() {
        claimDao.delete(this.testInsuranceClaim);
    }

    @Test
    public void generateFhirClaimItem_shouldMapInsuranceClaimItemToFhirClaimItem() {
        List<Claim.ItemComponent> generatedClaimItem =
                claimItemService.generateClaimItemComponent(this.testInsuranceClaim);

        Claim.ItemComponent actual = generatedClaimItem.get(0);
        Assert.assertThat(generatedClaimItem, Matchers.hasSize(1));
        Assert.assertThat(actual.getCategory().getText(), Matchers.equalTo("item"));
        Assert.assertThat(actual.getQuantity().getValue().intValue(),
                Matchers.equalTo(testInsuranceItem.getQuantityProvided()));
        Assert.assertThat(actual.getService().getText(), Matchers.equalTo(getExpectedCode()));
        Assert.assertThat(actual.getUnitPrice().getValue().floatValue(), Matchers.equalTo(10.10f));
    }

    @Test
    public void generateOmrsClaimItem_shouldMapFhirItemToInsuranceClaimItem() throws FHIRException {
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
        Concept concept = Context.getConceptService().getConceptByUuid("160148BAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
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

    private String getExpectedCode() {
        Concept concept = Context.getConceptService().getConceptByUuid("160148BAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        return concept.getConceptMappings().stream()
                .filter(c -> c.getConceptReferenceTerm()
                        .getUuid()
                        .equals(EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_UUID))
                .map(c -> c.getConceptReferenceTerm().toString())
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
