package org.openmrs.module.insuranceclaims.util;


import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.helper.DiagnosisHelper;
import org.openmrs.module.insuranceclaims.api.helper.InsuranceClaimHelper;

public class ContextUtil {

    public static DiagnosisHelper getDiagnosisHelper() {
        return Context.getRegisteredComponent("insuranceclaims.DiagnosisHelper", DiagnosisHelper.class);
    }

    public static InsuranceClaimHelper getInsuranceClaimHelper() {
        return Context.getRegisteredComponent(
                "insuranceclaims.InsuranceClaimHelper", InsuranceClaimHelper.class);
    }

    private ContextUtil() { }
}

