package team.a9043.sign_in_system.message;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system.convertor.JsonObjectHttpMessageConverter;

import javax.annotation.Resource;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MessageTest {
    @Value("${wxapp.rooturl}")
    String rooturl;
    @Resource
    JsonObjectHttpMessageConverter jsonObjectHttpMessageConverter;
    @Value("${wxapp.appid}")
    private String appid;
    @Value("${wxapp.secret}")
    private String secret;

    public RestTemplate getRestTemplate() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        return restTemplateBuilder
            .additionalMessageConverters(new StringHttpMessageConverter())
            .additionalMessageConverters(jsonObjectHttpMessageConverter)
            .build();
    }

    public String getAccessToken() {
        String format = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
        JSONObject jsonObject = getRestTemplate().getForObject(String.format(format, appid, secret), JSONObject.class);
        log.info(jsonObject.toString());
        return jsonObject.getString("access_token");
    }

    @Test
    public void addMessage() {
        String format = "https://api.weixin.qq.com/cgi-bin/wxopen/template/add?access_token=%s";
        ArrayList<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(3);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "AT0264");
        jsonObject.put("keyword_id_list", list);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString());
        JSONObject res = getRestTemplate().postForObject(String.format(format,
            "14_GyfnBPxignS2HPZSyyGVGOqbXcXZIKNC0Ba9XKEI02pgzKkhjdKeLOLYcrbwPufMSfVgtQW9_qj8iYUZL4Sq-YvUhYR6k55mDxWVQPLTUm5_V5YmKAcvku2oXCGh8vTyqnmScqNxB5KBcRRsBNHbAIAJVP"),
            httpEntity, JSONObject.class);
        log.info(res.toString(2));
    }
}
