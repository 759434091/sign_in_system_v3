package team.a9043.sign_in_system_version_2.config;

import org.json.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonObjectHttpMessageConverter extends AbstractHttpMessageConverter<JSONObject> {

    public JsonObjectHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8);
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return JSONObject.class.equals(aClass);
    }

    @Override
    protected JSONObject readInternal(Class<? extends JSONObject> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(JSONObject jsonObject, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        httpOutputMessage.getBody().write(jsonObject.toString().getBytes());
    }
}
