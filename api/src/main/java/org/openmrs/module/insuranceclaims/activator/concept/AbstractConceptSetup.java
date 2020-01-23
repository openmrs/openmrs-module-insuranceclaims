package org.openmrs.module.insuranceclaims.activator.concept;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;

import javax.transaction.Transactional;
import java.util.Locale;

@Transactional
public abstract class AbstractConceptSetup {

    private String conceptUuid;
    private ConceptName conceptName;
    private String conceptDataType;
    private String conceptClass;

    public void createConceptIfNotExist() {
        if (!isContextExisting()) {
            Concept quantityConsumedItems = buildConcept();
            saveConcept(quantityConsumedItems);
        }
    }

    protected AbstractConceptSetup(String conceptUuid, String conceptName,
                                   String dataTypeUuid, String conceptClassUuid) {
        this.conceptUuid = conceptUuid;
        this.conceptName = buildConceptName(conceptName);
        this.conceptDataType = dataTypeUuid;
        this.conceptClass = conceptClassUuid;
    }

    protected boolean isContextExisting() {
        return Context.getConceptService().getConceptByUuid(conceptUuid) != null;
    }

    protected Concept buildConcept() {
        Concept concept = new Concept();

        concept.setDatatype(Context.getConceptService().getConceptDatatypeByUuid(conceptDataType));
        concept.setConceptClass(Context.getConceptService().getConceptClassByUuid(conceptClass));
        concept.setFullySpecifiedName(conceptName);
        concept.setUuid(conceptUuid);
        return concept;
    }

    protected void saveConcept(Concept concept) {
        Context.getConceptService().saveConcept(concept);
    }

    private ConceptName buildConceptName(String conceptName) {
        ConceptName name = new ConceptName();

        name.setLocale(Locale.ENGLISH);
        name.setName(conceptName);
        name.setLocalePreferred(true);
        return name;
    }
}
