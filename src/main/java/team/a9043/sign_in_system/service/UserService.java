package team.a9043.sign_in_system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system.convertor.JsonObjectHttpMessageConverter;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.WxServerException;
import team.a9043.sign_in_system.mapper.SisSignInDetailMapper;
import team.a9043.sign_in_system.mapper.SisUserMapper;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.pojo.SisUserExample;
import team.a9043.sign_in_system.service_pojo.OperationResponse;
import team.a9043.sign_in_system.util.JwtUtil;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author a9043
 */
@Service
@Slf4j
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

    @SuppressWarnings("Duplicates")
    public JSONObject modifyBindUser(SisUser sisUser, String code) throws InvalidParameterException, WxServerException {
        SisUser stdSisUser =
            sisUserMapper.selectByPrimaryKey(sisUser.getSuId());
        if (null == stdSisUser) {
            throw new InvalidParameterException(
                "Token user not found: " + sisUser.getSuId());
        }

        if (null != stdSisUser.getSuOpenid()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("error", true);
            jsonObject.put("message", "User has bind");
            jsonObject.put("errCode", 1);
            jsonObject.put("token_user", new JSONObject(sisUser));
            return jsonObject;
        }

        JSONObject wxUserInfo = restTemplate.getForObject(String.format(
            apiurl, appid, secret, code
        ), JSONObject.class);

        if (null == wxUserInfo) {
            throw new WxServerException("WX Server error");
        }
        if (!wxUserInfo.has("openid")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("error", true);
            jsonObject.put("errCode", 2);
            jsonObject.put("message", wxUserInfo.toString());
            return jsonObject;
        }

        String openid = wxUserInfo.getString("openid");

        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria().andSuOpenidEqualTo(openid);
        if (!sisUserMapper.selectByExample(sisUserExample).isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("error", true);
            jsonObject.put("message", "User has bind");
            jsonObject.put("errCode", 1);
            jsonObject.put("token_user", new JSONObject(sisUser));
            return jsonObject;
        }

        SisUser updatedSisUser = new SisUser();
        updatedSisUser.setSuId(sisUser.getSuId());
        updatedSisUser.setSuOpenid(openid);
        sisUserMapper.updateByPrimaryKeySelective(updatedSisUser);

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("suId", sisUser.getSuId());
        claimsMap.put("suName", sisUser.getSuId());
        claimsMap.put("type", "code");
        claimsMap.put("suAuthoritiesStr",
            sisUser.getSuAuthoritiesStr());

        log.info("User " + sisUser.getSuId() + " successfully bind openId: " + openid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("errCode", 0);
        jsonObject.put("access_token", JwtUtil.createJWT(claimsMap));
        return jsonObject;
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
                claimsMap.put("type", "code");
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


    public JSONObject getStudents(@Nullable Integer page,
                                  @Nullable Integer pageSize,
                                  @Nullable String suId,
                                  @Nullable String suName,
                                  @Nullable Boolean orderByCozLackNum) throws IncorrectParameterException {
        if (null == page) {
            throw new IncorrectParameterException("Incorrect page: " + null);
        }
        if (page < 1)
            throw new IncorrectParameterException("Incorrect page: " + page +
                " (must equal or bigger than 1)");
        if (null == pageSize)
            pageSize = 10;
        else if (pageSize <= 0 || pageSize > 500) {
            throw new IncorrectParameterException(
                "pageSize must between [1, 500]");
        }

        SisUserExample sisUserExample = new SisUserExample();
        SisUserExample.Criteria criteria = sisUserExample.createCriteria();
        criteria.andSuAuthoritiesStrLike("%STUDENT%");

        if (null != orderByCozLackNum) {
            sisUserExample.setOrderByCozLackNum(orderByCozLackNum);
        }
        if (null != suId) {
            criteria.andSuIdLike("%" + suId + "%");
        }
        if (null != suName) {
            criteria.andSuNameLike(CourseService.getFuzzySearch(suName));
        }

        PageHelper.startPage(page, pageSize);
        List<SisUser> sisUserList =
            sisUserMapper.selectByExample(sisUserExample);
        PageInfo<SisUser> pageInfo = new PageInfo<>(sisUserList);

        List<String> suIdList = sisUserList.parallelStream()
            .map(SisUser::getSuId)
            .distinct()
            .collect(Collectors.toList());

        if (suIdList.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("page", page);
            jsonObject.put("message", "No student");
            return jsonObject;
        }

        JSONObject pageJson = new JSONObject(pageInfo);
        pageJson.getJSONArray("list")
            .forEach(sisUserObj -> {
                JSONObject sisUserJson = (JSONObject) sisUserObj;
                sisUserJson.remove("suPassword");
            });

        log.info("UserService.getStudents(..) success");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("page", page);
        jsonObject.put("data", pageJson);
        return jsonObject;
    }

    @Transactional
    public JSONObject modifyPassword(String suId,
                                     String oldPassword,
                                     String newPassword) throws IncorrectParameterException {
        SisUser sisUser = sisUserMapper.selectByPrimaryKey(suId);
        if (null == sisUser)
            throw new IncorrectParameterException("No user found: " + suId);
        if (newPassword.length() < 6 || newPassword.length() > 18)
            throw new IncorrectParameterException("Invalid new password");

        if (!bCryptPasswordEncoder.matches(oldPassword,
            sisUser.getSuPassword())) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "Incorrect password");
            return jsonObject;
        }

        sisUser.setSuPassword(bCryptPasswordEncoder.encode(newPassword));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            sisUserMapper.updateByPrimaryKey(sisUser) > 0);
        return jsonObject;
    }

    public JSONObject deleteUser(String suId) throws IncorrectParameterException {
        SisUser sisUser = sisUserMapper.selectByPrimaryKey(suId);
        if (null == sisUser)
            throw new IncorrectParameterException("User not found.");

        boolean success = sisUserMapper.deleteByPrimaryKey(suId) > 0;
        if (!success)
            log.error("Delete user Error: " + suId);
        else if (log.isDebugEnabled())
            log.debug("User delete success: " + suId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        return jsonObject;
    }
}

