package org.openmrs.module.insuranceclaims;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ABSOULUTE_HI_CONSUMED_ITEMS;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ABSOULUTE_LOW_CONSUMED_ITEMS;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMABLES_LIST_CONCEPT_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMABLES_LIST_ITEMS_CONCEPT_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMED_ITEMS_CONCEPT_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMED_ITEMS_CONCEPT_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMED_ITEMS_FORM_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.QUANTITY_CONSUMED_CONCEPT_NAME;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.QUANTITY_CONSUMED_CONCEPT_UUID;

/**
 * Contains the logic that is run every time this module is either started or stopped
 */
public class InsuranceClaimsActivator extends BaseModuleActivator implements DaemonTokenAware {

	private static final Logger LOG = LoggerFactory.getLogger(InsuranceClaimsActivator.class);

	private static final String MODULE_START_MESSAGE = "Started Insurance Claims";
	private static final String MODULE_STOP_MESSAGE  = "Stopped Insurance Claims";

	private static final String PATH_TO_CONSUMED_ITEM_FORM_TEMPLATE = "insuranceclaims:htmlforms/consumedItemFormTemplate.xml";

	private DaemonToken daemonToken;

	private EventListener eventListener;

	/**
	 * @see #started()
	 */
	@Override
	public void started() {

		if (Context.getConceptService().getConceptByUuid(CONSUMABLES_LIST_ITEMS_CONCEPT_UUID) == null) {
			createConsumableItemsListConcept();
		}

		if (Context.getConceptService().getConceptByUuid(QUANTITY_CONSUMED_CONCEPT_UUID) == null) {
			createQuantityConsumedConcept();
		}

		if (Context.getConceptService().getConceptByUuid(CONSUMED_ITEMS_CONCEPT_UUID) == null) {
			createComplexConsumedItemConcept();
		}

		HtmlFormEntryService service = Context.getService(HtmlFormEntryService.class);
		if (service.getHtmlFormByUuid(CONSUMED_ITEMS_FORM_UUID) == null) {
			try {
				setupHtmlForms();
			} catch (Exception e) {
				LOG.error("Failed to load consumed item form. Caused by:  " + e.toString());
			}
		}

		eventListener = getItemConsumedListener();
		Event.subscribe(Obs.class, Event.Action.CREATED.name(), eventListener);
		LOG.info(MODULE_START_MESSAGE);
	}

	/**
	 * @see #stopped()
	 */
	@Override
	public void stopped() {
		Event.unsubscribe(Obs.class, Event.Action.CREATED, eventListener);
		LOG.info(MODULE_STOP_MESSAGE);
	}

	@Override
	public void setDaemonToken(DaemonToken token) {
		daemonToken = token;
	}

	private void createConsumableItemsListConcept() {
		Concept consumedItems = new Concept();
		ConceptDatatype dataType = Context.getConceptService().getConceptDatatypeByUuid(ConceptDatatype.CODED_UUID);
		ConceptClass conceptClass = Context.getConceptService().getConceptClassByUuid(ConceptClass.FINDING_UUID);
		ConceptName name = buildConceptName(CONSUMABLES_LIST_CONCEPT_NAME);

		consumedItems.setDatatype(dataType);
		consumedItems.setConceptClass(conceptClass);
		consumedItems.setFullySpecifiedName(name);
		consumedItems.setUuid(CONSUMABLES_LIST_ITEMS_CONCEPT_UUID);

		Context.getConceptService().saveConcept(consumedItems);
	}

	private void createComplexConsumedItemConcept() {
		Concept complexConcept = new Concept();
		ConceptDatatype dataType = Context.getConceptService().getConceptDatatypeByUuid(ConceptDatatype.N_A_UUID);
		ConceptClass conceptClass = Context.getConceptService().getConceptClassByUuid(ConceptClass.CONVSET_UUID);
		ConceptName name = buildConceptName(CONSUMED_ITEMS_CONCEPT_NAME);

		complexConcept.setDatatype(dataType);
		complexConcept.setConceptClass(conceptClass);
		complexConcept.setFullySpecifiedName(name);
		complexConcept.setUuid(CONSUMED_ITEMS_CONCEPT_UUID);

		complexConcept.addSetMember(Context.getConceptService().getConceptByUuid(CONSUMABLES_LIST_ITEMS_CONCEPT_UUID));
		complexConcept.addSetMember(Context.getConceptService().getConceptByUuid(QUANTITY_CONSUMED_CONCEPT_UUID));

		Context.getConceptService().saveConcept(complexConcept);
	}

	private void createQuantityConsumedConcept() {
		Concept quantityConsumed = new Concept();
		ConceptDatatype dataType = Context.getConceptService().getConceptDatatypeByUuid(ConceptDatatype.NUMERIC_UUID);
		ConceptClass conceptClass = Context.getConceptService().getConceptClassByUuid(ConceptClass.MISC_UUID);
		ConceptName name = buildConceptName(QUANTITY_CONSUMED_CONCEPT_NAME);

		quantityConsumed.setDatatype(dataType);
		quantityConsumed.setConceptClass(conceptClass);
		quantityConsumed.setFullySpecifiedName(name);
		quantityConsumed.setUuid(QUANTITY_CONSUMED_CONCEPT_UUID);

		Context.getConceptService().saveConcept(quantityConsumed);
		ConceptNumeric numericConcept = Context.getConceptService().getConceptNumeric(quantityConsumed.getConceptId());
		numericConcept.setLowAbsolute(ABSOULUTE_LOW_CONSUMED_ITEMS);
		numericConcept.setHiAbsolute(ABSOULUTE_HI_CONSUMED_ITEMS);
		Context.getConceptService().saveConcept(numericConcept);
	}

	private ConceptName buildConceptName(String conceptName) {
		ConceptName name = new ConceptName();

		name.setLocale(Locale.ENGLISH);
		name.setName(conceptName);
		name.setLocalePreferred(true);
		return name;
	}

	private EventListener getItemConsumedListener() {
		return new ItemConsumedEventListener(daemonToken);
	}

	private void setupHtmlForms() throws IOException {
		ResourceFactory resourceFactory = ResourceFactory.getInstance();
		FormService formService = Context.getFormService();
		HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);

		HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService,
				htmlFormEntryService, PATH_TO_CONSUMED_ITEM_FORM_TEMPLATE);
	}

}
