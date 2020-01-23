package org.openmrs.module.insuranceclaims;

import org.openmrs.Obs;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.FormService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.insuranceclaims.activator.concept.ModuleConceptSetup;
import org.openmrs.module.insuranceclaims.util.ConstantValues;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONSUMED_ITEMS_FORM_UUID;

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
		addConcepts();

		HtmlFormEntryService service = Context.getService(HtmlFormEntryService.class);
		if (service.getHtmlFormByUuid(CONSUMED_ITEMS_FORM_UUID) == null) {
			try {
				setupHtmlForms();
			} catch (Exception e) {
				LOG.error("Failed to load consumed item form. Caused by:  " + e.toString());
			}
		}

		createInsureNumberAttribute();

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

	private void addConcepts() {
		Context.getService(ModuleConceptSetup.class).createConcepts();
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

	private void createInsureNumberAttribute() {
		PersonAttributeType attributeType = new PersonAttributeType();
		attributeType.setName(ConstantValues.POLICY_NUMBER_ATTRIBUTE_TYPE_NAME);
		attributeType.setFormat(ConstantValues.POLICY_NUMBER_ATTRIBUTE_TYPE_FORMAT);
		attributeType.setDescription(ConstantValues.POLICY_NUMBER_ATTRIBUTE_TYPE_DESCRIPTION);
		attributeType.setUuid(ConstantValues.POLICY_NUMBER_ATTRIBUTE_TYPE_UUID);
		createPersonAttributeTypeIfNotExists(attributeType);
	}

	private void createPersonAttributeTypeIfNotExists(PersonAttributeType attributeType) {
		PersonService personService = Context.getPersonService();
		PersonAttributeType actual = personService.getPersonAttributeTypeByUuid(attributeType.getUuid());
		if (actual == null) {
			personService.savePersonAttributeType(attributeType);
		}
	}
}
