package team.a9043.sign_in_system_version_2.config;

import org.json.JSONArray;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonArrayHttpmessageConverter extends AbstractHttpMessageConverter<JSONArray> {

    public JsonArrayHttpmessageConverter() {
        super(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return JSONArray.class.equals(clazz);
    }

    @Override
    protected JSONArray readInternal(Class<? extends JSONArray> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(JSONArray objects, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        outputMessage.getBody().write(objects.toString().getBytes());
    }
}
