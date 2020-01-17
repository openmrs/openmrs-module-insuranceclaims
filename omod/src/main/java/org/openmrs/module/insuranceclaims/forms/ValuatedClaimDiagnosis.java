package org.openmrs.module.insuranceclaims.forms;

import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

public class ValuatedClaimDiagnosis {
    private String diagnosisName;
    private String diagnosisUuid;

    public ValuatedClaimDiagnosis() {}

    public ValuatedClaimDiagnosis(InsuranceClaimDiagnosis diagnosis) {
        this.diagnosisName = diagnosis.getConcept().getName().getName();
        this.diagnosisUuid = diagnosis.getUuid();
    }
    public void setDiagnosisName(String diagnosisName) {
        this.diagnosisName = diagnosisName;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }

    public String getDiagnosisUuid() {
        return diagnosisUuid;
    }

    public void setDiagnosisUuid(String diagnosisUuid) {
        this.diagnosisUuid = diagnosisUuid;
    }
}
