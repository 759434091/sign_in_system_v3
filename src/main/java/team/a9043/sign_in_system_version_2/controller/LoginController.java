package team.a9043.sign_in_system_version_2.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import team.a9043.sign_in_system_version_2.exception.TokenErrorException;
import team.a9043.sign_in_system_version_2.pojo.User;
import team.a9043.sign_in_system_version_2.service.LoginService;
import team.a9043.sign_in_system_version_2.tokenuser.TokenUser;
import team.a9043.sign_in_system_version_2.util.GeetestLib;
import team.a9043.sign_in_system_version_2.util.UserPasswordEncrypt;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class LoginController {
    private static final String CAPTCHA_ID = "b142b065129f424b8f22e93fdecbfa70";
    private static final String CAPTCHA_KEY = "a41176bcedadc7c23c795cff4c63cf34";
    private static final boolean NEW_FAIL_BACK = true;
    private final RedisTemplate redisTemplate;
    private final LoginService loginService;

    @Autowired
    public LoginController(RedisTemplate redisTemplate, LoginService loginService) {
        this.redisTemplate = redisTemplate;
        this.loginService = loginService;
    }

    /**
     * 获得当前周
     *
     * @return JSONObject
     */
    @RequestMapping(value = "/week", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public JSONObject getCurWeek() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currentWeek", loginService.getCurWeek(LocalDateTime.now()));
        return jsonObject;
    }

    /**
     * 账号密码登录以及微信绑定
     *
     * @param map   code, usrId, usrPwd
     * @param token header token
     * @return JSONObject
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject doLogin(@RequestBody Map<String, String> map, @RequestHeader(value = "Access-Token") String token) throws TokenErrorException, JSONException {
        return loginService.userLogin(map, token);
    }

    /**
     * 微信登录
     *
     * @param map code
     * @return JSONObject
     */
    @RequestMapping(value = "/wx-user", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject wxLogin(@RequestBody Map<String, String> map) throws JSONException {
        return loginService.wxLogin(map);
    }

    /**
     * 检测登录状态
     *
     * @param user 学生用户
     * @return true 活动, false 已销毁
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public JSONObject checkLogin(@TokenUser(required = false) User user) {
        //未登录
        if (user == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("login", false);
            return jsonObject;
        }

        //保护数据
        user.setOpenId(null);
        user.setUsrPwd(null);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", true);
        jsonObject.put("user", new JSONObject(user));
        return jsonObject;
    }

    /**
     * 登出用户
     *
     * @param token token
     * @return true 登出, false 失败, null 非法token
     */
    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public JSONObject doLogout(@RequestHeader(value = "Access-Token", required = false) String token) {
        //错误检测
        if (token == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", false);
            jsonObject.put("msg", "fail");
            return jsonObject;
        }

        //登出
        JSONObject jsonObject = new JSONObject();
        boolean res = redisTemplate.delete("token_" + token);
        jsonObject.put("status", res);
        jsonObject.put("msg", res ? "success" : "fail");
        return jsonObject;
    }

    /**
     * 修改密码
     *
     * @param user 学生用户
     * @param map  old_pwd, new_pwd
     * @return true 成功, false 失败
     */
    @RequestMapping(value = "/user", method = RequestMethod.PATCH)
    public JSONObject changePassword(@TokenUser User user, @RequestBody Map<String, String> map) {
        String enOldPwd = UserPasswordEncrypt.encrypt(map.get("oldPwd"));
        String enNewPwd = UserPasswordEncrypt.encrypt(map.get("newPwd"));
        boolean res = enOldPwd.equals(user.getUsrPwd()) && loginService.changePassword(user, enNewPwd);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", res);
        jsonObject.put("msg", res ? "success" : "error");
        return jsonObject;
    }

    /**
     * 验证码初始化模块
     *
     * @param map 无需管理
     * @return 无需管理
     */
    @RequestMapping(value = "/start-captcha", method = RequestMethod.GET)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public String startValidate(@RequestParam Map<String, String> map) {
        GeetestLib gtSdk = new GeetestLib(CAPTCHA_ID, CAPTCHA_KEY, NEW_FAIL_BACK);
        HashMap<String, String> param = new HashMap<>();
        param.put("client_type", map.get("client_type")); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        param.put("ip_address", map.get("ip_address")); //传输用户请求验证时所携带的IP
        int gtServerStatus = gtSdk.preProcess(param);
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForHash().put("token_" + token, gtSdk.gtServerStatusSessionKey, gtServerStatus);
        redisTemplate.expire("token_" + token, 60 * 10, TimeUnit.SECONDS);
        JSONObject jsonObject = new JSONObject(gtSdk.getResponseStr());
        jsonObject.put("token", token);
        return jsonObject.toString();
    }

    /**
     * 验证码二次验证
     *
     * @param map   无需管理
     * @param token token
     * @return 无需管理
     */
    @RequestMapping(value = "/verify-captcha", method = RequestMethod.POST)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public String verifyCaptchaCode(@RequestBody Map<String, String> map, @RequestHeader(value = "Access-Token") String token) {
        GeetestLib gtSdk = new GeetestLib(CAPTCHA_ID, CAPTCHA_KEY, NEW_FAIL_BACK);
        String challenge = map.get(GeetestLib.fn_geetest_challenge);
        String validate = map.get(GeetestLib.fn_geetest_validate);
        String seccode = map.get(GeetestLib.fn_geetest_seccode);
        HashMap<String, String> param = new HashMap<>();
        param.put("client_type", map.get("client_type")); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        param.put("ip_address", map.get("ip_address")); //传输用户请求验证时所携带的IP
        // 从session中获取gt-server状态
        int gt_server_status_code = Optional
                .ofNullable((int) redisTemplate.opsForHash().get("token_" + token, gtSdk.gtServerStatusSessionKey))
                .orElseThrow(() -> new TokenErrorException("captcha failed"));
        int gtResult;
        if (gt_server_status_code == 1) {
            // gt-server正常，向gt-server进行二次验证
            gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
        } else {
            // gt-server非正常情况下，进行failback模式验证
            gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
        }
        if (gtResult != 1) {
            throw new TokenErrorException("captcha failed");
        }
        redisTemplate.delete("token_" + token);
        redisTemplate.opsForValue().set("token_" + token, "success", 60 * 10, TimeUnit.SECONDS);
        JSONObject data = new JSONObject();
        data.put("status", true);
        data.put("msg", "success");
        data.put("version", gtSdk.getVersionInfo());
        return data.toString();
    }

}
