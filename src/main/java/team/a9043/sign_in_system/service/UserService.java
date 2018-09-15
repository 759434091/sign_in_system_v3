package team.a9043.sign_in_system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.NonNull;
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
    //TODO doc data
    public OperationResponse modifyBindUser(SisUser sisUser, String code) throws InvalidParameterException, WxServerException {
        SisUser stdSisUser =
            sisUserMapper.selectByPrimaryKey(sisUser.getSuId());
        if (null == stdSisUser) throw new InvalidParameterException(
            "Token user not found: " + sisUser.getSuId());

        if (null != stdSisUser.getSuOpenid()) {
            OperationResponse operationResponse = new OperationResponse();
            operationResponse.setSuccess(false);
            operationResponse.setCode(1);
            operationResponse.setMessage("User has bind");
            return operationResponse;
        }

        JSONObject wxUserInfo = restTemplate.getForObject(
            String.format(apiurl, appid, secret, code), JSONObject.class);

        if (null == wxUserInfo) throw new WxServerException("WX Server error");
        if (!wxUserInfo.has("openid")) {
            OperationResponse operationResponse = new OperationResponse();
            operationResponse.setSuccess(false);
            operationResponse.setData(wxUserInfo.toString());
            operationResponse.setCode(2);
            return operationResponse;
        }

        String openid = wxUserInfo.getString("openid");

        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria().andSuOpenidEqualTo(openid);
        if (!sisUserMapper.selectByExample(sisUserExample).isEmpty()) {
            OperationResponse operationResponse = new OperationResponse();
            operationResponse.setSuccess(false);
            operationResponse.setCode(1);
            operationResponse.setMessage("User has bind");
            return operationResponse;
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
        OperationResponse operationResponse = new OperationResponse();
        operationResponse.setSuccess(true);
        operationResponse.setData(JwtUtil.createJWT(claimsMap));
        operationResponse.setCode(0);
        operationResponse.setMessage("data -> access_token");
        return operationResponse;
    }

    /**
     * 小程序获取Token 接口
     * 补充Spring Security
     *
     * @param code 微信 code
     * @return JSON
     * @throws WxServerException 微信服务器错误
     */
    public OperationResponse getTokensByCode(String code) throws WxServerException {
        JSONObject wxUserInfo = restTemplate.getForObject(
            String.format(apiurl, appid, secret, code), JSONObject.class);

        if (null == wxUserInfo) throw new WxServerException("WX Server error");
        if (!wxUserInfo.has("openid"))
            return new OperationResponse(false, wxUserInfo.toString());

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

                OperationResponse operationResponse = new OperationResponse();
                operationResponse.setSuccess(true);
                operationResponse.setMessage("data => access_token");
                operationResponse.setData(JwtUtil.createJWT(claimsMap));
                return operationResponse;
            })
            .orElseGet(() -> new OperationResponse(false,
                String.format("No user found " + "by openid %s", openid)));
    }


    public PageInfo<SisUser> getStudents(@NonNull Integer page,
                                         @NonNull Integer pageSize,
                                         @Nullable String suId,
                                         @Nullable String suName,
                                         @Nullable Boolean orderByCozLackNum) throws IncorrectParameterException {
        if (null == page)
            throw new IncorrectParameterException("Incorrect page: " + null);
        if (page < 1)
            throw new IncorrectParameterException("Incorrect page: " + page +
                " (must equal or bigger than 1)");
        if (pageSize <= 0 || pageSize > 500)
            throw new IncorrectParameterException(
                "pageSize must between [1, 500]");

        SisUserExample sisUserExample = new SisUserExample();
        SisUserExample.Criteria criteria = sisUserExample.createCriteria();
        criteria.andSuAuthoritiesStrLike("%STUDENT%");

        if (null != orderByCozLackNum)
            sisUserExample.setOrderByCozLackNum(orderByCozLackNum);
        if (null != suId)
            criteria.andSuIdLike("%" + suId + "%");
        if (null != suName)
            criteria.andSuNameLike(CourseService.getFuzzySearch(suName));

        PageHelper.startPage(page, pageSize);
        List<SisUser> sisUserList =
            sisUserMapper.selectByExample(sisUserExample);
        PageInfo<SisUser> pageInfo = new PageInfo<>(sisUserList);

        List<String> suIdList = sisUserList.parallelStream()
            .map(SisUser::getSuId)
            .distinct()
            .collect(Collectors.toList());

        if (suIdList.isEmpty()) return pageInfo;

        pageInfo.getList().forEach(u -> u.setSuPassword(null));
        log.info("UserService.getStudents(..) success");
        return pageInfo;
    }

    @Transactional
    public OperationResponse modifyPassword(String suId,
                                            String oldPassword,
                                            String newPassword) throws IncorrectParameterException {
        SisUser sisUser = sisUserMapper.selectByPrimaryKey(suId);
        if (null == sisUser)
            throw new IncorrectParameterException("No user found: " + suId);
        if (newPassword.length() < 6 || newPassword.length() > 18)
            throw new IncorrectParameterException("Invalid new password");

        if (!bCryptPasswordEncoder
            .matches(oldPassword, sisUser.getSuPassword()))
            return new OperationResponse(false, "Incorrect password");

        sisUser.setSuPassword(bCryptPasswordEncoder.encode(newPassword));
        sisUserMapper.updateByPrimaryKey(sisUser);
        return OperationResponse.SUCCESS;
    }

    public OperationResponse deleteUser(String suId) throws IncorrectParameterException {
        SisUser sisUser = sisUserMapper.selectByPrimaryKey(suId);
        if (null == sisUser)
            throw new IncorrectParameterException("User not found.");

        sisUserMapper.deleteByPrimaryKey(suId);
        log.debug("User delete success: " + suId);
        return OperationResponse.SUCCESS;
    }
}

