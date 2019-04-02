package org.openmrs.module.insuranceclaims.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the links that will appear on the administration page under the
 * "insuranceclaims.title" heading. This extension is enabled by defining (uncommenting) it in the
 * config.xml file.
 */
public class AdminList extends AdministrationSectionExt {

	private static final String MODULE_TITLE_KEY = "insuranceclaims.title";

	private static final String MODULE_REFAPP_TITLE = "insuranceclaims.refapp.title";

	private static final String MODULE_REFAPP_UI_URL = "insuranceclaims/insuranceclaims.page";

	private static final String MODULE_LEGACY_UI_URL = "module/insuranceclaims/insuranceclaims.form";

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
	 */
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getTitle()
	 */
	@Override
	public String getTitle() {
		return MODULE_TITLE_KEY;
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
	 */
	@Override
	public Map<String, String> getLinks() {

		Map<String, String> map = new HashMap<>();

		map.put(MODULE_LEGACY_UI_URL, MODULE_TITLE_KEY);
		map.put(MODULE_REFAPP_UI_URL, MODULE_REFAPP_TITLE);

		return map;
	}

}
