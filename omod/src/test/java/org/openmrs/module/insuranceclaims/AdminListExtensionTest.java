package org.openmrs.module.insuranceclaims;

import org.junit.Test;
import org.openmrs.module.Extension;
import org.openmrs.module.insuranceclaims.extension.html.AdminList;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * This test validates the AdminList extension class
 */
public class AdminListExtensionTest extends BaseModuleWebContextSensitiveTest {
	
	/**
	 * Get the links for the extension class
	 */
	@Test
	public void getLinks_shouldReturnNonEmptyList() {
		AdminList ext = new AdminList();
		
		Map<String, String> links = ext.getLinks();
		
		assertThat(links, is(notNullValue()));
		assertThat(links.size(), is(not(0)));
	}
	
	/**
	 * Check the media type of this extension class
	 */
	@Test
	public void getMediaType_shouldReturnHTMLType() {
		AdminList ext = new AdminList();
		
		assertThat(ext.getMediaType(), is(Extension.MEDIA_TYPE.html));
	}
	
}
