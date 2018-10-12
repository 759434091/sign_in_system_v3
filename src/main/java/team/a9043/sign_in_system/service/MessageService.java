package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system.mapper.SisJoinCourseMapper;
import team.a9043.sign_in_system.mapper.SisUserMapper;
import team.a9043.sign_in_system.pojo.SisJoinCourse;
import team.a9043.sign_in_system.pojo.SisJoinCourseExample;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.pojo.SisUserExample;
import team.a9043.sign_in_system.service_pojo.AppToken;
import team.a9043.sign_in_system.service_pojo.FormId;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService {
    @Resource
    private AppToken appToken;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;
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

    public void sendSignInMessage(String scId) {
        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();
        sisJoinCourseExample.createCriteria().andScIdEqualTo(scId);
        List<SisJoinCourse> sisJoinCourseList = sisJoinCourseMapper.selectByExample(sisJoinCourseExample);
        if (sisJoinCourseList.isEmpty()) return;

        List<String> suIdList = sisJoinCourseList.stream().map(SisJoinCourse::getSuId).collect(Collectors.toList());
        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria().andSuIdIn(suIdList);
        List<SisUser> sisUserList = sisUserMapper.selectByExample(sisUserExample);

        List<String> openidList = sisUserList.stream().map(SisUser::getSuOpenid).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (openidList.isEmpty()) return;


        String urlFormat = "/cgi-bin/message/wxopen/template/send?access_token=%s";
        String signInTemplateId = "J-dpmRqRsK2H6TI_lLE_90Z3U8xKBAiArSM5Prn7D14";
        CompletableFuture[] completableFutures = openidList.stream()
            .map(openid -> CompletableFuture.runAsync(() -> {
                String key = String.format(formIdKeyFormat, openid);

                FormId formId;
                while ((formId = (FormId) sisRedisTemplate.opsForList().rightPop(key)) == null || formId.getLocalDateTime().isBefore(LocalDateTime.now()))
                    if (sisRedisTemplate.opsForList().size(key) == null || sisRedisTemplate.opsForList().size(key) <= 0)
                        return;

                JSONObject data = new JSONObject();
                data.put("keyword1", "课程");
                data.put("keyword2", "结束时间");
                data.put("keyword3", "内容");
                JSONObject request = new JSONObject();
                request.put("template_id", signInTemplateId);
                request.put("page", "/");
                request.put("touser", openid);
                request.put("data", data);
                request.put("form_id", formId.getFormId());

                HttpEntity<JSONObject> httpEntity = new HttpEntity<>(request);
                JSONObject res = restTemplate.postForObject(String.format(urlFormat, appToken.getAccessToken()), httpEntity, JSONObject.class);
                if (null != res)
                    log.info(res.toString());
            }))
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(completableFutures);
    }
}
