package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
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
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class InsuranceClaimItemDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private InsuranceClaimItemDao dao;

	@Test
	public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
		InsuranceClaimItem claimItem = createTestInstance();

		dao.saveOrUpdate(claimItem);

		Context.flushSession();
		Context.clearSession();

		InsuranceClaimItem savedClaimItem = dao.getByUuid(claimItem.getUuid());

		Assert.assertThat(savedClaimItem, hasProperty("uuid", is(claimItem.getUuid())));
		Assert.assertThat(savedClaimItem, hasProperty("quantityProvided", is(claimItem.getQuantityProvided())));
		Assert.assertThat(savedClaimItem, hasProperty("quantityApproved", is(claimItem.getQuantityApproved())));
		Assert.assertThat(savedClaimItem, hasProperty("priceApproved", is(claimItem.getPriceApproved())));
		Assert.assertThat(savedClaimItem, hasProperty("explanation", is(claimItem.getExplanation())));
		Assert.assertThat(savedClaimItem, hasProperty("justification", is(claimItem.getJustification())));
		Assert.assertThat(savedClaimItem, hasProperty("rejectionReason", is(claimItem.getRejectionReason())));
		Assert.assertThat(savedClaimItem, hasProperty("item", is(claimItem.getItem())));
		Assert.assertThat(savedClaimItem, hasProperty("insuranceClaim", is(claimItem.getInsuranceClaim())));
		Assert.assertThat(savedClaimItem, hasProperty("claimItemStatus", is(claimItem.getClaimItemStatus())));
	}

	private InsuranceClaimItem createTestInstance() {
		Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
		Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
		Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
		VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
		PatientIdentifierType identifierType = Context.getPatientService()
				.getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
		InsuranceClaim insuranceClaim = InsuranceClaimMother.createTestInstance(location, provider, visitType,
				identifierType);
		return InsuranceClaimItemMother.createTestInstance(concept, insuranceClaim);
	}
}
