package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system.mapper.SisUserMapper;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.service_pojo.AppToken;
import team.a9043.sign_in_system.service_pojo.FormId;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class MessageService {
    @Resource
    private AppToken appToken;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private SisUserMapper sisUserMapper;
    @Resource(name = "sisRedisTemplate")
    private RedisTemplate<String, Object> sisRedisTemplate;
    private String formIdKeyFormat = "sis_formid_openid_%s";

    public void receiveFormId(SisUser sisUser, String formIdStr) {
        if (null == sisUser.getSuOpenid()) return;

        String key = String.format(formIdKeyFormat, sisUser.getSuOpenid());
        FormId formId = new FormId(formIdStr, LocalDateTime.now().plus(6, ChronoUnit.DAYS));

        sisRedisTemplate.opsForList().leftPush(key, formId);
        sisRedisTemplate.opsForList().trim(key, 0, 50);
        log.info("success add: " + sisUser.getSuOpenid() + " , " + formIdStr);
    }

    public void sendSignInMessage(String suId) {
        String urlFormat = "/cgi-bin/message/wxopen/template/send?access_token=%s";
        String signInTemplateId = "J-dpmRqRsK2H6TI_lLE_90Z3U8xKBAiArSM5Prn7D14";

        SisUser sisUser = sisUserMapper.selectByPrimaryKey(suId);
        if (null == sisUser || null == sisUser.getSuOpenid()) return;

        String key = String.format(formIdKeyFormat, sisUser.getSuOpenid());
        FormId formId = (FormId) sisRedisTemplate.opsForList().rightPop(key);
        if (null == formId || formId.getLocalDateTime().isBefore(LocalDateTime.now())) return;

        JSONObject data = new JSONObject();
        data.put("keyword1", "课程");
        data.put("keyword2", "结束时间");
        data.put("keyword3", "内容");
        JSONObject request = new JSONObject();
        request.put("template_id", signInTemplateId);
        request.put("page", "/");
        request.put("touser", sisUser.getSuOpenid());
        request.put("data", data);
        request.put("form_id", formId.getFormId());

        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(request);
        JSONObject res = restTemplate.postForObject(String.format(urlFormat,
            "14_nmVwRxWnbCwvdXS1D4jD1kDLsvZyfOpHdTiD1Vk1qNEw4Uw3HYy53HKABOfTiu2Iw2csVPX1RkNLy2Xyh4Z0WaY4VidXB2JIfNbCppP3DMaCofHGOZtNA3-C8_lHVT8RSkI5knyuEF1idH4ySXFaABAGLT"),
            httpEntity, JSONObject.class);
        /*JSONObject res = restTemplate.postForObject(String.format(urlFormat, appToken.getAccessToken()), httpEntity, JSONObject.class);*/
        log.info(res.toString());
    }
}
