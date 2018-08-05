package team.a9043.sign_in_system.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system.convertor.JsonObjectHttpMessageConverter;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.WxServerException;
import team.a9043.sign_in_system.repository.SisUserRepository;
import team.a9043.sign_in_system.util.JwtUtil;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * @author a9043
 */
@Service
public class UserService {
    private RestTemplate restTemplate;
    @Value("${wxapp.apiurl}")
    private String apiurl;
    @Value("${wxapp.appid}")
    private String appid;
    @Value("${wxapp.secret}")
    private String secret;
    @Resource
    private SisUserRepository sisUserRepository;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(@Value("${wxapp.rooturl}") String rooturl,
                       @Autowired JsonObjectHttpMessageConverter jsonObjectHttpMessageConverter) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplate = restTemplateBuilder
            .additionalMessageConverters(jsonObjectHttpMessageConverter)
            .rootUri(rooturl)
            .build();
    }

    /**
     * 新增用户
     *
     * @param sisUser 表单用户
     * @return 操作结果
     */
    @Transactional
    public boolean createUser(SisUser sisUser) {
        sisUser.setSuPassword(bCryptPasswordEncoder.encode(sisUser.getSuPassword()));
        return null != sisUserRepository.save(sisUser);
    }

    public JSONObject getTokensByCode(String code) throws WxServerException {
        JSONObject jsonObject = new JSONObject();
        JSONObject wxUserInfo = restTemplate.getForObject(String.format(
            apiurl, appid, secret, code
        ), JSONObject.class);

        if (null == wxUserInfo) {
            throw new WxServerException("WX Server error");
        }
        if (!wxUserInfo.has("openid")) {
            jsonObject.put("success", false);
            jsonObject.put("error", true);
            jsonObject.put("message", wxUserInfo.toString());
            return jsonObject;
        }

        String openid = wxUserInfo.getString("openid");
        SisUser exampleSisUser = new SisUser();
        exampleSisUser.setSuOpenid(openid);

        return sisUserRepository
            .findOne(Example.of(exampleSisUser))
            .map(sisUser -> {
                Map<String, Object> claimsMap = new HashMap<>();
                claimsMap.put("suId", sisUser.getSuId());
                claimsMap.put("suName", sisUser.getSuId());
                claimsMap.put("suAuthoritiesStr",
                    sisUser.getSuAuthoritiesStr());

                jsonObject.put("success", true);
                jsonObject.put("error", false);
                jsonObject.put("access_token", JwtUtil.createJWT(claimsMap));
                return jsonObject;
            })
            .orElseGet(() -> {
                String message = String
                    .format("No user found " + "by openid %s",
                        openid);
                jsonObject.put("success", false);
                jsonObject.put("error", true);
                jsonObject.put("message", message);
                return jsonObject;
            });
    }
}
