package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system.mapper.SisCourseMapper;
import team.a9043.sign_in_system.mapper.SisJoinCourseMapper;
import team.a9043.sign_in_system.mapper.SisUserMapper;
import team.a9043.sign_in_system.pojo.*;
import team.a9043.sign_in_system.service_pojo.AppToken;
import team.a9043.sign_in_system.service_pojo.FormId;
import team.a9043.sign_in_system.util.SisScheduleUtil;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
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
    @Resource
    private SisCourseMapper sisCourseMapper;
    private String formIdKeyFormat = "sis_formid_openid_%s";

    public void receiveFormId(SisUser sisUser, String formIdStr) {
        SisUser stdUser = sisUserMapper.selectByPrimaryKey(sisUser.getSuId());
        if (null == stdUser || null == stdUser.getSuOpenid()) return;

        String key = String.format(formIdKeyFormat, stdUser.getSuOpenid());
        FormId formId = new FormId(formIdStr, LocalDateTime.now().plus(6, ChronoUnit.DAYS));

        sisRedisTemplate.opsForList().leftPush(key, formId);
        sisRedisTemplate.opsForList().trim(key, 0, 50);
        log.info("success add: " + stdUser.getSuOpenid() + " , " + formIdStr);
    }

    @SuppressWarnings("ConstantConditions")
    @Async
    public void sendSignInMessage(SisSchedule sisSchedule, LocalDateTime signInEndTime) {
        SisCourse sisCourse = sisCourseMapper.selectByPrimaryKey(sisSchedule.getScId());
        if (null == sisCourse) return;

        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();
        sisJoinCourseExample.createCriteria().andScIdEqualTo(sisSchedule.getScId());
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
        String schTime = String.format(SisScheduleUtil.timeFormat,
            SisScheduleUtil.fortMap.get(sisSchedule.getSsFortnight()),
            sisSchedule.getSsStartWeek(),
            sisSchedule.getSsEndWeek(),
            SisScheduleUtil.dayMap.get(sisSchedule.getSsDayOfWeek()),
            sisSchedule.getSsStartTime(),
            sisSchedule.getSsEndTime(),
            sisSchedule.getSsRoom());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        CompletableFuture[] completableFutures = openidList.stream()
            .map(openid -> CompletableFuture.runAsync(() -> {
                String key = String.format(formIdKeyFormat, openid);

                FormId formId;
                while ((formId = (FormId) sisRedisTemplate.opsForList().rightPop(key)) == null || formId.getLocalDateTime().isBefore(LocalDateTime.now()))
                    if (sisRedisTemplate.opsForList().size(key) == null || sisRedisTemplate.opsForList().size(key) <= 0)
                        return;

                JSONObject kw1 = new JSONObject();
                kw1.put("value", sisCourse.getScName());
                JSONObject kw2 = new JSONObject();
                kw2.put("value", df.format(signInEndTime));
                JSONObject kw3 = new JSONObject();
                kw3.put("value", schTime);
                JSONObject data = new JSONObject();
                data.put("keyword1", kw1);
                data.put("keyword2", kw2);
                data.put("keyword3", kw3);
                JSONObject request = new JSONObject();
                request.put("template_id", signInTemplateId);
                request.put("page", "/");
                request.put("touser", openid);
                request.put("data", data);
                request.put("emphasis_keyword", "keyword1.DATA");
                request.put("form_id", formId.getFormId());

                HttpEntity<JSONObject> httpEntity = new HttpEntity<>(request);
                restTemplate.postForObject(String.format(urlFormat, appToken.getAccessToken()), httpEntity, JSONObject.class);
            }))
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(completableFutures)
            .whenComplete((r, e) -> {
                if (null != e)
                    log.info(e.getMessage());
                else
                    log.info("success sendSignInMessage: " + sisCourse.getScId() + ", " + sisSchedule.getSsId());
            });
    }

    @SuppressWarnings("ConstantConditions")
    public void sendSignInMessage(SisUser sisUser) {
        String urlFormat = "/cgi-bin/message/wxopen/template/send?access_token=%s";
        String signInTemplateId = "J-dpmRqRsK2H6TI_lLE_90Z3U8xKBAiArSM5Prn7D14";

        CompletableFuture.runAsync(() -> {
            String key = String.format(formIdKeyFormat, sisUser.getSuOpenid());

            FormId formId;
            while ((formId = (FormId) sisRedisTemplate.opsForList().rightPop(key)) == null || formId.getLocalDateTime().isBefore(LocalDateTime.now()))
                if (sisRedisTemplate.opsForList().size(key) == null || sisRedisTemplate.opsForList().size(key) <= 0)
                    return;

            JSONObject kw1 = new JSONObject();
            kw1.put("value", "课程");
            JSONObject kw2 = new JSONObject();
            kw2.put("value", "结束时间");
            JSONObject kw3 = new JSONObject();
            kw3.put("value", "内容");
            JSONObject data = new JSONObject();
            data.put("keyword1", kw1);
            data.put("keyword2", kw2);
            data.put("keyword3", kw3);
            JSONObject request = new JSONObject();
            request.put("template_id", signInTemplateId);
            request.put("page", "/");
            request.put("touser", sisUser.getSuOpenid());
            request.put("data", data);
            request.put("emphasis_keyword", "keyword1.DATA");
            request.put("form_id", formId.getFormId());

            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(request);
            JSONObject res = restTemplate.postForObject(String.format(urlFormat, appToken.getAccessToken()), httpEntity, JSONObject.class);
            if (null != res)
                log.info(res.toString());
        }).join();
    }
}
