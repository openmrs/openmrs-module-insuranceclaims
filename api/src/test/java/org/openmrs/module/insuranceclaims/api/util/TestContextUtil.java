package org.openmrs.module.insuranceclaims.api.util;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.insuranceclaims.api.helper.DiagnosisHelper;
import org.openmrs.module.insuranceclaims.api.helper.InsuranceClaimHelper;
import org.openmrs.module.insuranceclaims.util.ContextUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class TestContextUtil extends BaseModuleContextSensitiveTest {

    @Test
    public void testGetDiagnosisHelperInstance() {
        DiagnosisHelper x = ContextUtil.getDiagnosisHelper();

        Assert.assertNotNull(x);
        Assert.assertThat(x, Matchers.instanceOf(DiagnosisHelper.class));
    }

    @Test
    public void testGetInsuranceClaimHelperInstance() {
        InsuranceClaimHelper x = ContextUtil.getInsuranceClaimHelper();

        Assert.assertNotNull(x);
        Assert.assertThat(x, Matchers.instanceOf(InsuranceClaimHelper.class));
    }
}
