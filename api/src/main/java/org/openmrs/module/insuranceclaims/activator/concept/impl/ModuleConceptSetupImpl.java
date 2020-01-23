package org.openmrs.module.insuranceclaims.activator.concept.impl;

import org.openmrs.module.insuranceclaims.activator.concept.AbstractConceptSetup;
import org.openmrs.module.insuranceclaims.activator.concept.ModuleConceptSetup;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class ModuleConceptSetupImpl implements ModuleConceptSetup {

    private List<AbstractConceptSetup> setupConcepts;

    @Override
    public void createConcepts() {
        setupConcepts.forEach(AbstractConceptSetup::createConceptIfNotExist);
    }

    public void setSetupConcepts(List<AbstractConceptSetup> setupConcepts) {
        this.setupConcepts = setupConcepts;
    }
}
