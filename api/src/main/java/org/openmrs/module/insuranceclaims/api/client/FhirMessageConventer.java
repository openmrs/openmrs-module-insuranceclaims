package org.openmrs.module.insuranceclaims.api.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.mchange.v1.io.InputStreamUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Generic message conventer that use application/json type and allows all FHIR Models as response type
 */
public class FhirMessageConventer extends AbstractHttpMessageConverter<IBaseResource> {

    private static final String CHARSET = "UTF-8";
    private static final String TYPE = "application";
    private static final String SUBTYPE_1 = "json";

    private IParser parser = FhirContext.forDstu3().newJsonParser();

    public FhirMessageConventer() {
        super(new MediaType(TYPE, SUBTYPE_1, Charset.forName(CHARSET)));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return IBaseResource.class.isAssignableFrom(clazz);
    }

    @Override
    protected IBaseResource readInternal(Class<? extends IBaseResource> clazz, HttpInputMessage inputMessage)
            throws HttpMessageNotReadableException {
        try {
            String json = convertStreamToString(inputMessage.getBody());
            return parser.parseResource(json);
        }
        catch (IOException e) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
        }
    }

    @Override
    protected void writeInternal(IBaseResource resource, HttpOutputMessage outputMessage)
            throws HttpMessageNotWritableException {
        try {
            String json = parser.encodeResourceToString(resource);
            outputMessage.getBody().write(json.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new HttpMessageNotWritableException("Could not serialize object. Msg: " + e.getMessage(), e);
        }
    }

    private String convertStreamToString(InputStream is) throws IOException {
        return InputStreamUtils.getContentsAsString(is);
    }
}
