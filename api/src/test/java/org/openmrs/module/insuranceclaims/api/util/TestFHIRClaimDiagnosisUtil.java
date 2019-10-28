package org.openmrs.module.insuranceclaims.api.util;

import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.CodeableConcept;
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
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimDiagnosisMother;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.testutils.TestConstants;
import org.openmrs.module.insuranceclaims.util.FHIRClaimDiagnosisUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;


public class TestFHIRClaimDiagnosisUtil extends BaseModuleContextSensitiveTest {

    private InsuranceClaim testInsuranceClaim;
    private InsuranceClaimDiagnosis testInsuranceDiagnosis;

    @Autowired
    private InsuranceClaimDiagnosisDao diagnosisDao;

    @Autowired
    private InsuranceClaimDao claimDao;

    @Before
    public void setUp() {
        Context.flushSession();
        Context.clearSession();

        this.testInsuranceClaim = createTestClaim();
        this.testInsuranceDiagnosis = createTestClaimDiagnosis();

        claimDao.saveOrUpdate(this.testInsuranceClaim);
        diagnosisDao.saveOrUpdate(this.testInsuranceDiagnosis);

    }

    @After
    public void tearDown() {
        diagnosisDao.delete(this.testInsuranceDiagnosis);
        claimDao.delete(this.testInsuranceClaim);
    }

    @Test
    public void testCreateFHIRDiagnosis() throws FHIRException {
        Claim.DiagnosisComponent diagnosisComponent =
                FHIRClaimDiagnosisUtil.createClaimDiagnosisComponent(this.testInsuranceDiagnosis);

        CodeableConcept test = diagnosisComponent.getDiagnosisCodeableConcept();

        String icd10coding = test.getCoding().stream()
                .filter(c -> c.getSystem().equals("http://hl7.org/fhir/sid/icd-10"))
                .findFirst()
                .get()
                .getCode();

        Assert.assertThat(test.getText(), Matchers.equalTo("Malaria, confirmed"));
        Assert.assertThat(icd10coding, Matchers.equalTo("B53.8"));
    }

    @Test
    public void testCreateFHIRDiagnosisList() throws FHIRException {
        List<InsuranceClaimDiagnosis> testDiagnisis = Collections.singletonList(this.testInsuranceDiagnosis);

        List<Claim.DiagnosisComponent> diagnosisComponent =
                FHIRClaimDiagnosisUtil.createClaimDiagnosisComponent(testDiagnisis);
        String transformedComponentName = diagnosisComponent.get(0).getDiagnosisCodeableConcept().getText();

        Assert.assertThat(diagnosisComponent, Matchers.hasSize(1));
        Assert.assertThat(transformedComponentName, Matchers.equalTo("Malaria, confirmed"));
    }

    @Test
    public void testCreateClaimDiagnosis() {
        Claim.DiagnosisComponent fhirDiagnosis = FHIRClaimDiagnosisUtil
                .createClaimDiagnosisComponent(this.testInsuranceDiagnosis);
        List<String> errors = new LinkedList<>();

        InsuranceClaimDiagnosis created = FHIRClaimDiagnosisUtil.createOmrsClaimDiagnosis(fhirDiagnosis, errors);

        Assert.assertThat(created.getUuid(), Matchers.equalTo(this.testInsuranceDiagnosis.getUuid()));
        Assert.assertThat(created.getConcept(), Matchers.equalTo(this.testInsuranceDiagnosis.getConcept()));
        Assert.assertThat(errors, Matchers.hasSize(0));
    }

    private InsuranceClaim createTestClaim() {
        Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
        Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
        VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
        return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
    }

    private InsuranceClaimDiagnosis createTestClaimDiagnosis() {
        try {
            String conceptPath = "test_malaria_concept.xml";
            executeDataSet(conceptPath);
        } catch (Exception e) {
            Logger log = Logger.getGlobal();
            log.warning("Problem with loading concept from malaria-concept.xml: \n"
                    + e.getStackTrace().toString());
        }
        Concept concept = Context.getConceptService().getConceptByUuid("160148AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return InsuranceClaimDiagnosisMother.createTestInstance(concept, this.testInsuranceClaim);
    }

}
