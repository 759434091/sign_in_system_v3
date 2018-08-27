package team.a9043.sign_in_system.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system.convertor.JsonObjectHttpMessageConverter;
import team.a9043.sign_in_system.exception.WxServerException;
import team.a9043.sign_in_system.mapper.SisUserMapper;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.pojo.SisUserExample;
import team.a9043.sign_in_system.util.JwtUtil;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private SisUserMapper sisUserMapper;

    public UserService(@Value("${wxapp.rooturl}") String rooturl,
                       @Autowired JsonObjectHttpMessageConverter jsonObjectHttpMessageConverter) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplate = restTemplateBuilder
            .additionalMessageConverters(jsonObjectHttpMessageConverter)
            .rootUri(rooturl)
            .build();
    }

    @Deprecated
    @SuppressWarnings("ConstantConditions")
    public JSONObject getUser(SisUser sisUser) throws InvalidParameterException {
        return Optional
            .ofNullable(sisUserMapper.selectByPrimaryKey(sisUser.getSuId()))
            .map(stdSisUser -> {
                stdSisUser.setSuPassword(null);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", true);
                jsonObject.put("error", false);
                jsonObject.put("user", new JSONObject(stdSisUser));
                return jsonObject;
            })
            .orElseThrow(() -> new InvalidParameterException(
                "Token user not found: " + sisUser.getSuId()));
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
        return sisUserMapper.insert(sisUser) > 0;
    }

    @SuppressWarnings("Duplicates")
    public JSONObject modifyBindUser(SisUser sisUser, String code) throws WxServerException {
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
        return Optional
            .ofNullable(sisUserMapper.selectByPrimaryKey(sisUser.getSuId()))
            .map(stdSisUser -> {
                SisUser updatedSisUser = new SisUser();
                updatedSisUser.setSuId(sisUser.getSuId());
                updatedSisUser.setSuOpenid(openid);
                sisUserMapper.updateByPrimaryKeySelective(updatedSisUser);

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
                jsonObject.put("success", false);
                jsonObject.put("error", true);
                jsonObject.put("message", "Token user not found ");
                jsonObject.put("token_user", new JSONObject(sisUser));
                return jsonObject;
            });
    }

    /**
     * 小程序获取Token 接口
     * 补充Spring Security
     *
     * @param code 微信 code
     * @return JSON
     * @throws WxServerException 微信服务器错误
     */
    @SuppressWarnings({"Duplicates", "ConstantConditions"})
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
        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria().andSuOpenidEqualTo(openid);


        return sisUserMapper.selectByExample(sisUserExample)
            .stream()
            .findAny()
            .map(sisUser -> {
                Map<String, Object> claimsMap = new HashMap<>();
                claimsMap.put("suId", sisUser.getSuId());
                claimsMap.put("suName", sisUser.getSuName());
                claimsMap.put("suAuthoritiesStr",
                    sisUser.getSuAuthoritiesStr());

                sisUser.setSuPassword(null);

                jsonObject.put("user", new JSONObject(sisUser));
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
