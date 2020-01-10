package org.openmrs.module.insuranceclaims;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAttribute;
import org.openmrs.ConceptAttributeType;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimItem;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ClaimUtilsTest extends BaseModuleWebContextSensitiveTest {

    private static final String IS_SERVICE_ATTR_UUID = "925e4987-3104-4d74-989b-3ec96197b532";
    private static final String PRICE_ATTR_UUID = "ddc082c8-db30-4796-890e-f0d487fb9085";
    private static final String TEST_CONCEPT_NAME = "Test concept";
    private static final String TEST_CLAIM_ITEM_UUID = "TestUuid";
    private static final Float PRICE_ATTR_VALUE = 20.00f;
    private static final Boolean IS_SERVICE_ATTR_VALUE = false;

    private ConceptAttributeType priceAttributeType;
    private ConceptAttributeType isServiceAttributeType;

    @Before
    public void setUpClass() {
        createTestAttributeTypes();
    }

    @Test
    public void getInsuranceClaimItemsAsMap_shouldFormatClaimToMapReadableByTemplate() {
        InsuranceClaimItem testItem = getTestInsuranceClaimItem();
        List<InsuranceClaimItem> items = Arrays.asList(testItem, testItem);
        Map<String, List<InsuranceClaimItem>> result = ClaimUtils.getInsuranceClaimItemsAsMap(items);

        Set<String> keys = result.keySet();
        Assert.assertThat(keys, Matchers.hasSize(1));
        String key = keys.iterator().next();

        Assert.assertThat(key, Matchers.containsString(TEST_CONCEPT_NAME));
        Assert.assertThat(key, Matchers.containsString(PRICE_ATTR_VALUE.toString()));
        Assert.assertThat(key, Matchers.containsString(IS_SERVICE_ATTR_VALUE.toString()));
        Assert.assertThat(result.values(), Matchers.hasSize(1));
        Assert.assertThat(result.get(key), Matchers.hasSize(2));
    }

    @Test
    public void getProvidedItemsAsMap_shouldFormatClaimToMapReadableByTemplate() {
        ProvidedItem testItem = getTestProvidedItem();
        List<ProvidedItem> items = Arrays.asList(testItem, testItem);
        Map<String, List<ProvidedItem>> result = ClaimUtils.getProvidedItemsAsMap(items);

        Set<String> keys = result.keySet();
        Assert.assertThat(keys, Matchers.hasSize(1));
        String key = keys.iterator().next();

        Assert.assertThat(key, Matchers.containsString(TEST_CONCEPT_NAME));
        Assert.assertThat(key, Matchers.containsString(PRICE_ATTR_VALUE.toString()));
        Assert.assertThat(key, Matchers.containsString(IS_SERVICE_ATTR_VALUE.toString()));
        Assert.assertThat(result.values(), Matchers.hasSize(1));
        Assert.assertThat(result.get(key), Matchers.hasSize(2));
    }

    private void createTestAttributeTypes() {
        ConceptAttributeType priceType = new ConceptAttributeType();
        priceType.setName("Item/Price");
        priceType.setUuid(PRICE_ATTR_UUID);
        priceType.setDatatypeClassname("org.openmrs.customdatatype.datatype.FloatDatatype");
        ConceptAttributeType isService = new ConceptAttributeType();
        isService.setName("Is service");
        isService.setUuid(IS_SERVICE_ATTR_UUID);
        isService.setDatatypeClassname("org.openmrs.customdatatype.datatype.BooleanDatatype");

        Context.getConceptService().saveConceptAttributeType(priceType);
        Context.getConceptService().saveConceptAttributeType(isService);
        this.priceAttributeType = priceType;
        this.isServiceAttributeType = isService;
    }

    private InsuranceClaimItem getTestInsuranceClaimItem() {
        InsuranceClaimItem item = new InsuranceClaimItem();
        item.setClaim(new InsuranceClaim());
        item.setItem(getTestProvidedItem());
        item.setUuid(TEST_CLAIM_ITEM_UUID);
        return item;
    }

    private ProvidedItem getTestProvidedItem() {
        ProvidedItem providedItem = new ProvidedItem();
        Concept newConcept = createTestConcept();

        providedItem.setPrice(new BigDecimal("20.00"));
        providedItem.setItem(newConcept);
        return providedItem;
    }

    private Concept createTestConcept() {
        Concept newConcept = new Concept();

        newConcept.setAttributes(getTestConceptAttributes(newConcept));
        ConceptDatatype datatype = Context.getConceptService().getConceptDatatypeByUuid(ConceptDatatype.N_A_UUID);
        ConceptClass conceptClass = Context.getConceptService().getConceptClass(1);
        newConcept.setDatatype(datatype);
        newConcept.setConceptClass(conceptClass);


        ConceptName conceptName = new ConceptName(TEST_CONCEPT_NAME, Locale.ENGLISH);
        newConcept.setFullySpecifiedName(conceptName);
        Context.getConceptService().saveConcept(newConcept);
        return newConcept;
    }

    private Set<ConceptAttribute> getTestConceptAttributes(Concept concept) {
        ConceptAttribute conceptPrice = new ConceptAttribute();
        conceptPrice.setAttributeType(this.priceAttributeType);
        conceptPrice.setValue(PRICE_ATTR_VALUE);
        conceptPrice.setConcept(concept);

        ConceptAttribute isService = new ConceptAttribute();
        isService.setAttributeType(this.isServiceAttributeType);
        isService.setValue(IS_SERVICE_ATTR_VALUE);
        isService.setConcept(concept);

        List<ConceptAttribute> conceptAttributes = new ArrayList<>();
        conceptAttributes.add(conceptPrice);
        conceptAttributes.add(isService);

        return new HashSet<>(conceptAttributes);
    }

}
