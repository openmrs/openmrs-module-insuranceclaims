package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimItemDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimItemMother;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class InsuranceClaimItemDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private InsuranceClaimItemDao dao;

	@Test
	public void saveInsuranceClaimItem_shouldSaveAllPropertiesInDb() {
		InsuranceClaimItem claimItem = InsuranceClaimItemMother.createTestInstance();

		dao.saveInsuranceClaimItem(claimItem);

		Context.flushSession();
		Context.clearSession();

		InsuranceClaimItem savedClaimItem = dao.getInsuranceClaimItemByUuid(claimItem.getUuid());

		Assert.assertThat(savedClaimItem, hasProperty("uuid", is(claimItem.getUuid())));
		Assert.assertThat(savedClaimItem, hasProperty("quantityProvided", is(claimItem.getQuantityProvided())));
		Assert.assertThat(savedClaimItem, hasProperty("quantityApproved", is(claimItem.getQuantityApproved())));
		Assert.assertThat(savedClaimItem, hasProperty("priceApproved", is(claimItem.getPriceApproved())));
		Assert.assertThat(savedClaimItem, hasProperty("priceAsked", is(claimItem.getPriceAsked())));
		Assert.assertThat(savedClaimItem, hasProperty("explanation", is(claimItem.getExplanation())));
		Assert.assertThat(savedClaimItem, hasProperty("justification", is(claimItem.getJustification())));
		Assert.assertThat(savedClaimItem, hasProperty("rejectionReason", is(claimItem.getRejectionReason())));
		Assert.assertThat(savedClaimItem, hasProperty("item", is(claimItem.getItem())));
		Assert.assertEquals(claimItem.getInsuranceClaim(), savedClaimItem.getInsuranceClaim());
		Assert.assertThat(savedClaimItem, hasProperty("claimItemStatus", is(claimItem.getClaimItemStatus())));

		InsuranceClaimItem sameClaimItem = dao.getInsuranceClaimItemById(savedClaimItem.getId());
		Assert.assertEquals(savedClaimItem, sameClaimItem);
	}
}
