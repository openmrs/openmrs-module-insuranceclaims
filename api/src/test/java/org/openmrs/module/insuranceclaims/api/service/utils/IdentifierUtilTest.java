package org.openmrs.module.insuranceclaims.api.service.utils;

import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IdentifierUtilTest extends BaseModuleContextSensitiveTest {

    private static final String TEST_CODE = "TEST_CODE";

    private static final String TEST_SYSTEM = "TEST_SYSTEM";

    private static final String TEST_IDENTIFIER_VALUE = "1D3NtIFI3R";

    private static final String REFERENCE_PREFIX = "isIt";

    private static final String REFERENCE_SUFFIX = "Reference";

    @Test
    public void getClaimIdentifierValueBySystemCode_shouldReturnIdentifierValue() {
        Claim testClaim = createTestClaim();
        String actualIdentifierValue= IdentifierUtil.getClaimIdentifierValueBySystemCode(testClaim, TEST_CODE);

        Assert.assertThat(actualIdentifierValue, Matchers.equalTo(TEST_IDENTIFIER_VALUE));
    }

    @Test
    public void getClaimResponseIdentifierValueBySystemCode_shouldReturnIdentifierValue() {
        ClaimResponse testClaim = createTestClaimResponse();
        String actualIdentifierValue= IdentifierUtil.getClaimIdentifierValueBySystemCode(testClaim, TEST_CODE);

        Assert.assertThat(actualIdentifierValue, Matchers.equalTo(TEST_IDENTIFIER_VALUE));
    }

    @Test
    public void createIdentifer_shouldCreateCorrectIdentifer() {
        Identifier actualIdentifier= IdentifierUtil.createIdentifier(TEST_IDENTIFIER_VALUE, TEST_CODE, TEST_SYSTEM);
        Identifier expectedIdentifier = createTestIdentifier(TEST_CODE, TEST_SYSTEM, TEST_IDENTIFIER_VALUE);

        Assert.assertThat(actualIdentifier.getValue(), Matchers.equalTo(expectedIdentifier.getValue()));
    }

    @Test
    public void buildReference_shouldCreateCorrectReference() {
        String expectedReferenceString = REFERENCE_PREFIX + "/" + REFERENCE_SUFFIX;
        Reference actualReference = IdentifierUtil.buildReference(REFERENCE_PREFIX, REFERENCE_SUFFIX);
        Assert.assertThat(actualReference.getReference(), Matchers.equalTo(expectedReferenceString));
    }

    @Test
    public void getItFromReference_shouldReturnReferenceId() {
        Reference testReferecne = createTestReference();

        String expectedId = REFERENCE_SUFFIX;
        String actualId = IdentifierUtil.getIdFromReference(testReferecne);

        Assert.assertThat(expectedId, Matchers.equalTo(actualId));
    }

    @Test
    public void getUnambigiousElement_shouldReturnDistinctStringFromList() {
        List<String> testStringList = Collections.singletonList(TEST_IDENTIFIER_VALUE);

        String result = IdentifierUtil.getUnambiguousElement(testStringList);

        Assert.assertThat(result,Matchers.notNullValue());
        Assert.assertThat(result,Matchers.equalTo(TEST_IDENTIFIER_VALUE));
    }

    @Test
    public void getUnambigiousElement_shouldReturnDistinctIdentifierFromListWithTwoSameIdentifer() {
        Identifier testIdentifier = IdentifierUtil.createIdentifier(TEST_IDENTIFIER_VALUE, TEST_CODE, TEST_SYSTEM);
        List<Identifier> testList = Arrays.asList(testIdentifier, testIdentifier);

        Identifier result = IdentifierUtil.getUnambiguousElement(testList);

        Assert.assertThat(result,Matchers.notNullValue());
        Assert.assertThat(result.getValue(),Matchers.equalTo(testIdentifier.getValue()));
    }

    @Test(expected = IdentifierNotUniqueException.class)
    public void getUnambigiousElement_shouldThrowExceptionInUnambigiousList() {
        String distinctString1 = "diff";
        String distinctString2 = "strings";

        List<String> testList = Arrays.asList(distinctString1, distinctString2);

        IdentifierUtil.getUnambiguousElement(testList);
    }

    private Claim createTestClaim() {
        Claim claim = new Claim();
        claim.setIdentifier(
                Collections.singletonList(createTestIdentifier(TEST_CODE, TEST_SYSTEM, TEST_IDENTIFIER_VALUE)));
        return claim;
    }

    private ClaimResponse createTestClaimResponse() {
        ClaimResponse claimResponse = new ClaimResponse();
        claimResponse.setIdentifier(
                Collections.singletonList(createTestIdentifier(TEST_CODE, TEST_SYSTEM, TEST_IDENTIFIER_VALUE)));
        return claimResponse;
    }

    private Identifier createTestIdentifier(String code, String system, String value) {
        Identifier identifier = new Identifier();
        identifier.setValue(value);
        identifier.setSystem(system);
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setCode(code);
        codeableConcept.setCoding(Collections.singletonList(coding));
        identifier.setType(codeableConcept);
        return identifier;
    }

    private Reference createTestReference() {
        Reference ref = new Reference();
        ref.setReference(REFERENCE_PREFIX + "/" + REFERENCE_SUFFIX);
        return ref;
    }
}
