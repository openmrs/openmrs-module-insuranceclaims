package org.openmrs.module.insuranceclaims.api.helpers.impl;

import org.hamcrest.Matchers;
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
import org.openmrs.module.insuranceclaims.api.helper.InsuranceClaimHelper;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimDiagnosisMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.testutils.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TestInsuranceClaimHelper extends BaseModuleContextSensitiveTest {

    @Autowired
    InsuranceClaimDao insuranceClaimDao;

    @Autowired
    InsuranceClaimDiagnosisDao insuranceClaimDiagnosisDao;

    @Autowired
    InsuranceClaimHelper insuranceClaimHelper;

    InsuranceClaim insuranceClaim;
    InsuranceClaimDiagnosis insuranceClaimDiagnosis;

    @Before
    public void setUp() {
        this.insuranceClaim = createTestInsuranceClaim();
        insuranceClaimDao.saveOrUpdate(this.insuranceClaim);

        this.insuranceClaimDiagnosis = createTestInsuranceClaimDiagnosis();
        insuranceClaimDiagnosisDao.saveOrUpdate(this.insuranceClaimDiagnosis);
    }

    @Test
    public void testGetInsuranceClaimDiagnosis() {
        List<InsuranceClaimDiagnosis> result = insuranceClaimHelper.getInsuranceClaimDiagnosis(this.insuranceClaim);

        Assert.assertThat(result, Matchers.hasSize(1));
        Assert.assertThat(result, Matchers.contains(this.insuranceClaimDiagnosis));
    }


    private InsuranceClaim createTestInsuranceClaim() {
        Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
        Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
        VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
        return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
    }

    private InsuranceClaimDiagnosis createTestInsuranceClaimDiagnosis() {
        Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);

        return InsuranceClaimDiagnosisMother.createTestInstance(concept, insuranceClaim);
    }

}
