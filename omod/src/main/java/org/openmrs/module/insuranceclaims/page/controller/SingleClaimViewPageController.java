package org.openmrs.module.insuranceclaims.page.controller;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.ProvidedItemService;
import org.openmrs.module.insuranceclaims.forms.ClaimFormBuilder;
import org.openmrs.module.insuranceclaims.forms.NewClaimForm;
import org.openmrs.module.insuranceclaims.forms.ValuatedClaimForm;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.openmrs.module.insuranceclaims.ClaimUtils.getProvidedItemsAsMap;

@Controller
@OpenmrsProfile(modules = { "uicommons:*.*" })
public class SingleClaimViewPageController {

	private static final String VIEW = "singleClaimView";

	private static final String VISIT_DIAGNOSES_UUID = "1284AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	private static final String DIAGNOSIS_CLASS_UUID = "8d4918b0-c2cc-11de-8d13-0010c6dffd0f";

	public String get(PageModel model,
					  @SpringBean UiSessionContext uiSessionContext,
					  @SpringBean ClaimFormBuilder claimFormBuilder,
					  @RequestParam(value = "patientId") int patientId,
					  @RequestParam(value = "claimUuid", required = false) String claimUuid) {
		Patient patient = Context.getPatientService().getPatient(patientId);

		addEmptyAttributesToModel(model);

		if (!StringUtils.isEmpty(claimUuid)) {
			ValuatedClaimForm claim = claimFormBuilder.generateClaimForm(claimUuid);
			model.addAttribute("valuatedClaim", claim);
		} else {
			List<Concept> patientDiagnoses = getPatientDiagnoses(patient);
			model.addAttribute("patientDiagnoses", patientDiagnoses);
			model.addAttribute("providedItems", getPatientProvidedItems(patientId));
			model.addAttribute("visitTypes", Context.getVisitService().getAllVisitTypes());
		}

		addPatientAttributesToModel(model, patient);
		model.addAttribute("provider", uiSessionContext.getCurrentProvider());

		return VIEW;
	}

	@RequestMapping(value = "/insurnaceclaims/submitClaim", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Object post(PageModel model,
					   @RequestBody(required = true) NewClaimForm form,
					   HttpServletRequest request, HttpServletResponse response) {
		model.addAttribute("result", form != null ? form : "Not found");
		return RestUtil.created(response, form);
	}

	private List<Concept> getPatientDiagnoses(Patient patient) {
		ObsService obsService = Context.getObsService();
		List<Obs> patientObservations = obsService.getObservationsByPerson(patient.getPerson());
		return patientObservations.stream()
				.filter(obs -> obs.getConcept().getUuid().equals(VISIT_DIAGNOSES_UUID)) //get only observations related to visit diagnosis, stored in problem list
				.map(obs -> obs.getValueCoded())
				.filter(answers -> answers.getConceptClass().getUuid().equals(DIAGNOSIS_CLASS_UUID))
				.collect(Collectors.toList());
	}

	private void addPatientAttributesToModel(PageModel model, Patient patient) {
		model.addAttribute("patientName",patient.getPersonName().toString());
		model.addAttribute("patientUuid", patient.getUuid());
	}

	private Map<String, List<ProvidedItem>> getPatientProvidedItems(int patientId) {
		ProvidedItemService providedItemService = Context.getService(ProvidedItemService.class);
		List<ProvidedItem> patientEnteredProvidedItems = providedItemService.getProvidedEnteredItems(patientId);
		return getProvidedItemsAsMap(patientEnteredProvidedItems);
	}

	private void addEmptyAttributesToModel(PageModel model) {
		model.addAttribute("valuatedClaim", null);
		model.addAttribute("patientDiagnoses", null);
		model.addAttribute("providedItems", null);
		model.addAttribute("valuatedClaim", null);
		model.addAttribute("patientDiagnoses", null);
		model.addAttribute("providedItems", null);
		model.addAttribute("provider",null);
		model.addAttribute("visitTypes",null);
	}

}
