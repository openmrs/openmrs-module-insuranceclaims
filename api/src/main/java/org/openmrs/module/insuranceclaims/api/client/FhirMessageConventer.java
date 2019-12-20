package org.openmrs.module.insuranceclaims.api.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Generic message conventer that use application/json type and allows all FHIR Models as response type
 */
public class FhirMessageConventer extends AbstractHttpMessageConverter<IBaseResource> {

    private static final String CHARSET = "UTF-8";
    private static final String TYPE = "application";
    private static final String SUBTYPE_1 = "json";
    private static final int BUFFER_SIZE = 1024;

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
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[BUFFER_SIZE];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }

                reader.close();
            }
            finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
