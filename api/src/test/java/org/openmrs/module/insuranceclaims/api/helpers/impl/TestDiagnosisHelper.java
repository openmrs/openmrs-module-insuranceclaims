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
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.helper.DiagnosisHelper;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimDiagnosisMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.testutils.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDiagnosisHelper extends BaseModuleContextSensitiveTest {

    @Autowired
    private InsuranceClaimDiagnosisDao insuranceClaimDiagnosisDao;

    @Autowired
    DiagnosisHelper diagnosisHelper;

    private InsuranceClaimDiagnosis insuranceClaimDiagnosis;

    @Before
    public void setUp() {
        this.insuranceClaimDiagnosis = createTestInsuranceClaimDiagnosis();
        insuranceClaimDiagnosisDao.saveOrUpdate(this.insuranceClaimDiagnosis);
    }

    @Test
    public void testSetInsuranceClaim() {
        InsuranceClaim newInsuranceClaim = createTestInsuranceClaim();
        diagnosisHelper.setInsuranceClaim(insuranceClaimDiagnosis, newInsuranceClaim);

        Assert.assertThat(insuranceClaimDiagnosis.getInsuranceClaim(), Matchers.equalTo(newInsuranceClaim));
    }

    @Test
    public void testSetConcept() {
        Concept newConcept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
        diagnosisHelper.setConcept(insuranceClaimDiagnosis, newConcept);

        Assert.assertThat(insuranceClaimDiagnosis.getConcept(), Matchers.equalTo(newConcept));
    }

    private InsuranceClaimDiagnosis createTestInsuranceClaimDiagnosis() {
        InsuranceClaim insuranceClaim = createTestInsuranceClaim();
        Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);

        return InsuranceClaimDiagnosisMother.createTestInstance(concept, insuranceClaim);
    }

    private InsuranceClaim createTestInsuranceClaim() {
        Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
        Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
        VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
        return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
    }

}
