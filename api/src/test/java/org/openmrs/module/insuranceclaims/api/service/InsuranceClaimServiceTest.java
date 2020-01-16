package org.openmrs.module.insuranceclaims.api.service;

import javassist.NotFoundException;
import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItemStatus;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimStatus;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimDiagnosisMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimItemMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.INSURANCE_CLAIM_TEST_ITEM_CONCEPT_DATASET;

/**
 * This is an integration test (extends BaseModuleContextSensitiveTest), which verifies logic in InsuranceClaimService.
 */
public class InsuranceClaimServiceTest extends BaseModuleContextSensitiveTest {

	private static final String UPDATED_ADJUSTMENT = "UpdatedAdjustment";
	private static final BigDecimal UPDATED_APPROVED_TOTAL = new BigDecimal("123.45");
	private static final String[] DATE_FORMATS = {"yyyy-mm-dd"};
	private static final String UPDATED_DATE = "2034-10-10";
	private static final String UPDATED_REJECTION_REASON = "UpdatedRejectionReason";
	private static final int UPDATED_QUANTITY_APPROVED = 19;
	private static final InsuranceClaimItemStatus UPDATED_CLAIM_ITEM_STATUS = InsuranceClaimItemStatus.REJECTED;

	private static final InsuranceClaimStatus UPDATED_STATUS = InsuranceClaimStatus.REJECTED;

	@Autowired
	private InsuranceClaimService insuranceClaimService;

	@Autowired
	private InsuranceClaimItemService insuranceClaimItemService;

	@Autowired
	private InsuranceClaimDiagnosisService insuranceClaimDiagnosisService;

	@Autowired
	private ProvidedItemService providedItemService;

	@Test
	public void saveInsuranceClaim_shouldCorrectlySaveClaim() {
		InsuranceClaim claim = createTestClaim();
		insuranceClaimService.saveOrUpdate(claim);
		InsuranceClaim savedClaim = insuranceClaimService.getByUuid(claim.getUuid());

		Assert.assertThat(savedClaim, hasProperty("uuid", is(claim.getUuid())));
		Assert.assertThat(savedClaim, hasProperty("provider", is(claim.getProvider())));
		Assert.assertThat(savedClaim, hasProperty("patient", is(claim.getPatient())));
		Assert.assertThat(savedClaim, hasProperty("location", is(claim.getLocation())));
		Assert.assertThat(savedClaim, hasProperty("claimCode", is(claim.getClaimCode())));
		Assert.assertThat(savedClaim, hasProperty("dateFrom", is(claim.getDateFrom())));
		Assert.assertThat(savedClaim, hasProperty("dateTo", is(claim.getDateTo())));
		Assert.assertThat(savedClaim, hasProperty("adjustment", is(claim.getAdjustment())));
		Assert.assertThat(savedClaim, hasProperty("claimedTotal", is(claim.getClaimedTotal())));
		Assert.assertThat(savedClaim, hasProperty("approvedTotal", is(claim.getApprovedTotal())));
		Assert.assertThat(savedClaim, hasProperty("dateProcessed", is(claim.getDateProcessed())));
		Assert.assertThat(savedClaim, hasProperty("explanation", is(claim.getExplanation())));
		Assert.assertThat(savedClaim, hasProperty("rejectionReason", is(claim.getRejectionReason())));
		Assert.assertThat(savedClaim, hasProperty("guaranteeId", is(claim.getGuaranteeId())));
		Assert.assertThat(savedClaim, hasProperty("visitType", is(claim.getVisitType())));
		Assert.assertThat(savedClaim, hasProperty("status", is(claim.getStatus())));
	}

	@Test
	public void getService_shouldReturnNotNullService() {
		InsuranceClaimService insuranceClaimService = Context.getService(InsuranceClaimService.class);
		Assert.assertThat(insuranceClaimService, is(notNullValue()));
	}

	@Test
	public void saveClaimItem_shouldCorrectlySaveClaim() throws Exception {
		InsuranceClaimItem item = createTestClaimItem();
		insuranceClaimItemService.saveOrUpdate(item);
		InsuranceClaimItem savedClaim = insuranceClaimItemService.getByUuid(item.getUuid());

		Assert.assertThat(savedClaim, Matchers.notNullValue());
	}

	@Test
	public void getItemService_shouldReturnNotNullService() {
		InsuranceClaimItemService itemService = Context.getService(InsuranceClaimItemService.class);
		Assert.assertThat(itemService, is(notNullValue()));
	}

	@Test
	public void saveClaimDiagnosis_shouldCorrectlySaveClaim() {
		InsuranceClaimDiagnosis diagnosis = createTestClaimDiagnosis();
		insuranceClaimDiagnosisService.saveOrUpdate(diagnosis);
		InsuranceClaimDiagnosis savedClaim = insuranceClaimDiagnosisService.getByUuid(diagnosis.getUuid());

		Assert.assertThat(savedClaim, Matchers.notNullValue());
	}

	@Test
	public void getDiagnosisService_shouldReturnNotNullService() {
		InsuranceClaimDiagnosisService service = Context.getService(InsuranceClaimDiagnosisService.class);
		Assert.assertThat(service, is(notNullValue()));
	}

	@Test
	public void updateClaim_shouldUpdateCorrectFields() throws ParseException {
		InsuranceClaim claimWithUpdates = createClaimWithUpdates();
		InsuranceClaim toBeUpdated = createTestClaim();

		InsuranceClaim updatedClaim = insuranceClaimService.updateClaim(toBeUpdated, claimWithUpdates);
		Assert.assertThat(updatedClaim.getAdjustment(), Matchers.equalTo(claimWithUpdates.getAdjustment()));
		Assert.assertThat(updatedClaim.getApprovedTotal(), Matchers.equalTo(claimWithUpdates.getApprovedTotal()));
		Assert.assertThat(updatedClaim.getDateProcessed(), Matchers.equalTo(claimWithUpdates.getDateProcessed()));
		Assert.assertThat(updatedClaim.getRejectionReason(), Matchers.equalTo(claimWithUpdates.getRejectionReason()));
		Assert.assertThat(updatedClaim.getStatus(), Matchers.equalTo(claimWithUpdates.getStatus()));
	}

	@Test
	public void updateInsuranceClaimItem_shouldUpdateCorrectFields() throws Exception {
		InsuranceClaimItem toBeUpdated = createTestClaimItem();
		InsuranceClaimItem itemWithUpdates = createClaimItemWithUpdates();

		InsuranceClaimItem updatedItem = insuranceClaimItemService.updateInsuranceClaimItem(toBeUpdated, itemWithUpdates);
		Assert.assertThat(updatedItem.getJustification(), Matchers.equalTo(itemWithUpdates.getJustification()));
		Assert.assertThat(updatedItem.getStatus(), Matchers.equalTo(itemWithUpdates.getStatus()));
		Assert.assertThat(updatedItem.getQuantityApproved(), Matchers.equalTo(itemWithUpdates.getQuantityApproved()));
		Assert.assertThat(updatedItem.getRejectionReason(), Matchers.equalTo(itemWithUpdates.getRejectionReason()));
	}

	@Test
	public void updateInsuranceClaimItems_shouldMatchAndUpdateItems() throws Exception {
		InsuranceClaimItem toBeUpdated = createTestClaimItem();
		InsuranceClaimItem itemWithUpdates = createClaimItemWithUpdates();
		insuranceClaimItemService.saveOrUpdate(toBeUpdated);
		insuranceClaimItemService.saveOrUpdate(itemWithUpdates);
		List<InsuranceClaimItem> listOfItemsToUpdate = Arrays.asList(toBeUpdated);
		List<InsuranceClaimItem> listOfItemsWithUpdate = Arrays.asList(itemWithUpdates);

		List<InsuranceClaimItem> updatedItems =
				insuranceClaimItemService.updateInsuranceClaimItems(listOfItemsToUpdate, listOfItemsWithUpdate);

		Assert.assertThat(updatedItems, Matchers.hasSize(1));
		InsuranceClaimItem updatedItem = updatedItems.get(0);
		Assert.assertThat(updatedItem.getJustification(), Matchers.equalTo(itemWithUpdates.getJustification()));
		Assert.assertThat(updatedItem.getStatus(), Matchers.equalTo(itemWithUpdates.getStatus()));
		Assert.assertThat(updatedItem.getQuantityApproved(), Matchers.equalTo(itemWithUpdates.getQuantityApproved()));
		Assert.assertThat(updatedItem.getRejectionReason(), Matchers.equalTo(itemWithUpdates.getRejectionReason()));
	}

	@Test(expected = NotFoundException.class)
	public void updateInsuranceClaimItems_invalidUpdateItemsShouldThrowException() throws Exception {
		InsuranceClaimItem toBeUpdated = createTestClaimItem();
		InsuranceClaimItem itemWithUpdates = createClaimItemWithUpdates();
		Concept notMatchingConcept = Context.getConceptService().getConceptByUuid("160148BAAAAAAAAAAAAAAAAAAAAAAAAAABBB");

		itemWithUpdates.getItem().setItem(notMatchingConcept);
		insuranceClaimItemService.saveOrUpdate(toBeUpdated);
		providedItemService.saveOrUpdate(itemWithUpdates.getItem());
		insuranceClaimItemService.saveOrUpdate(itemWithUpdates);
		List<InsuranceClaimItem> listOfItemsToUpdate = Arrays.asList(toBeUpdated);
		List<InsuranceClaimItem> listOfItemsWithUpdate = Arrays.asList(itemWithUpdates);

		insuranceClaimItemService.updateInsuranceClaimItems(listOfItemsToUpdate, listOfItemsWithUpdate);
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

		InsuranceClaim claim = createTestClaim();
		return InsuranceClaimItemMother.createTestInstance(concept, claim);
	}

	private InsuranceClaimDiagnosis createTestClaimDiagnosis() {
		Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
		InsuranceClaim claim = createTestClaim();
		return InsuranceClaimDiagnosisMother.createTestInstance(concept, claim);
	}

	private InsuranceClaim createClaimWithUpdates() throws ParseException {
		InsuranceClaim insuranceClaim = new InsuranceClaim();
		insuranceClaim.setAdjustment(UPDATED_ADJUSTMENT);
		insuranceClaim.setApprovedTotal(UPDATED_APPROVED_TOTAL);
		insuranceClaim.setDateProcessed(DateUtils.parseDate(UPDATED_DATE, DATE_FORMATS));
		insuranceClaim.setRejectionReason(UPDATED_REJECTION_REASON);
		insuranceClaim.setStatus(UPDATED_STATUS);

		return insuranceClaim;
	}

	private InsuranceClaimItem createClaimItemWithUpdates() throws Exception {
		InsuranceClaimItem insuranceClaimItem = createTestClaimItem();
		insuranceClaimItem.setQuantityApproved(UPDATED_QUANTITY_APPROVED);
		insuranceClaimItem.setStatus(UPDATED_CLAIM_ITEM_STATUS);
		insuranceClaimItem.setJustification(UPDATED_ADJUSTMENT);
		insuranceClaimItem.setRejectionReason(UPDATED_REJECTION_REASON);

		return insuranceClaimItem;
	}
}
