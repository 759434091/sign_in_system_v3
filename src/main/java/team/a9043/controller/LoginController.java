package team.a9043.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import team.a9043.JavaUtil.GeetestLib;
import team.a9043.JavaUtil.UserPasswordEncrypt;
import team.a9043.mvcaop.TokenUser;
import team.a9043.pojo.User;
import team.a9043.pojo.UserBindOpenId;
import team.a9043.service.LoginService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller("loginController")
public class LoginController {
    private final String API_URL_STR = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    private final String APP_ID = "wx5899a00bc0df64a8";
    private final String SECRET = "b801fd1c57c927a857b4d251b3eebee0";
    private static final String CAPTCHA_ID = "b142b065129f424b8f22e93fdecbfa70";
    private static final String CAPTCHA_KEY = "a41176bcedadc7c23c795cff4c63cf34";
    private static final boolean NEW_FAIL_BACK = true;
    private final RedisTemplate<String, Object> redisTemplate;

    private final LoginService loginService;

    @Autowired
    public LoginController(RedisTemplate<String, Object> redisTemplate, LoginService loginService) {
        this.redisTemplate = redisTemplate;
        this.loginService = loginService;
    }

    @RequestMapping(value = "/checkLogin", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Boolean checkLogin(@TokenUser(required = false) User user) {
        return user != null;
    }

    /**
     * 检验登录 返回登录信息
     *
     * @return 返回登录真实用户
     */
    @RequestMapping(value = "/login", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String doLogin(@RequestBody UserBindOpenId userBindOpenId, @RequestHeader(value = "Access-Token") String token) {
        if (redisTemplate.opsForValue().get("token_" + token) != null && redisTemplate.opsForValue().get("token_" + token).toString().equals("success")) {
            User tempUser = userBindOpenId.getUser();
            User user = loginService.userLogin(tempUser.getUserId(), tempUser.getUserPwd());
            String code = userBindOpenId.getCode();
            boolean isBind = false;
            JSONObject jsonObject = new JSONObject();
            if (user != null) {
                if (code != null) {
                    String url = String.format(API_URL_STR, APP_ID, SECRET, code);
                    String s = httpRequest(url);
                    JSONObject object = JSON.parseObject(s);
                    String openId = object.getString("openid");
                    if (openId != null) {
                        isBind = loginService.bindOpenId(openId, user.getUserId());
                    }
                }
                redisTemplate.opsForValue().set("token_" + token, user, 60 * 30, TimeUnit.SECONDS);
                user.setUserPwd(null);
                jsonObject.put("user", user);
                jsonObject.put("isBind", isBind);
                jsonObject.put("token", token);
            }
            return jsonObject.toJSONString();
        } else {
            return null;
        }
    }


    @RequestMapping("/logout")
    @ResponseBody
    public Boolean doLogout(@RequestHeader("Access-Token") String token) {
        return redisTemplate.delete("token_" + token);
    }

    @RequestMapping("/changePassword")
    @ResponseBody
    public Boolean changePassword(@TokenUser User user, @RequestParam("old_pwd") String oldPwd, @RequestParam("new_pwd") String newPwd) {

        String enOldPwd = UserPasswordEncrypt.encrypt(oldPwd);
        String enNewPwd = UserPasswordEncrypt.encrypt(newPwd);
        return enOldPwd.equals(user.getUserPwd()) && loginService.changePassword(user.getUserId(), enNewPwd);
    }

    @RequestMapping(value = "/wxLogin", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String wxLogin(@RequestParam("code") String code) {
        String url = String.format(API_URL_STR, APP_ID, SECRET, code);
        String s = httpRequest(url);
        JSONObject object = JSON.parseObject(s);
        String openId = object.getString("openid");
        if (openId != null) {
            User user = loginService.wxLogin(openId);
            if (user != null) {
                String token = UUID.randomUUID().toString().replace("-", "");
                redisTemplate.opsForValue().set("token_" + token, user, 60 * 30, TimeUnit.SECONDS);
                user.setUserPwd(null);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", user);
                jsonObject.put("isBind", true);
                jsonObject.put("token", token);
                return jsonObject.toJSONString();
            } else {
                String token = UUID.randomUUID().toString().replace("-", "");
                redisTemplate.opsForValue().set("token_" + token, "success", 60 * 10, TimeUnit.SECONDS);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", null);
                jsonObject.put("isBind", false);
                jsonObject.put("token", token);
                return jsonObject.toJSONString();
            }
        } else {
            return "{\"err\":\"err\",\"errStr\":" + s + "}";
        }
    }

    @RequestMapping(value = "/startCaptcha")
    @ResponseBody
    public String startValidate(@RequestParam Map<String, String> map) {
        GeetestLib gtSdk = new GeetestLib(CAPTCHA_ID, CAPTCHA_KEY, NEW_FAIL_BACK);
        HashMap<String, String> param = new HashMap<>();
        param.put("client_type", map.get("client_type")); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        param.put("ip_address", map.get("ip_address")); //传输用户请求验证时所携带的IP
        int gtServerStatus = gtSdk.preProcess(param);
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForHash().put("token_" + token, gtSdk.gtServerStatusSessionKey, gtServerStatus);
        redisTemplate.expire("token_" + token, 60 * 10, TimeUnit.SECONDS);
        JSONObject jsonObject = JSON.parseObject(gtSdk.getResponseStr());
        jsonObject.put("token", token);
        return JSON.toJSONString(jsonObject);
    }

    @RequestMapping(value = "/verifyCaptchaCode")
    @ResponseBody
    public String verifyCaptchaCode(@RequestParam Map<String, String> map, @RequestHeader(value = "Access-Token") String token) {
        GeetestLib gtSdk = new GeetestLib(CAPTCHA_ID, CAPTCHA_KEY, NEW_FAIL_BACK);
        String challenge = map.get(GeetestLib.fn_geetest_challenge);
        String validate = map.get(GeetestLib.fn_geetest_validate);
        String seccode = map.get(GeetestLib.fn_geetest_seccode);
        HashMap<String, String> param = new HashMap<>();
        param.put("client_type", map.get("client_type")); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        param.put("ip_address", map.get("ip_address")); //传输用户请求验证时所携带的IP
        // 从session中获取gt-server状态
        int gt_server_status_code = (int) redisTemplate.opsForHash().get("token_" + token, gtSdk.gtServerStatusSessionKey);
        int gtResult = 0;
        if (gt_server_status_code == 1) {
            // gt-server正常，向gt-server进行二次验证
            gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
        } else {
            // gt-server非正常情况下，进行failback模式验证
            gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
        }
        if (gtResult == 1) {
            redisTemplate.delete("token_" + token);
            redisTemplate.opsForValue().set("token_" + token, "success", 60 * 10, TimeUnit.SECONDS);
            JSONObject data = new JSONObject();
            data.put("status", "success");
            data.put("version", gtSdk.getVersionInfo());
            return data.toJSONString();
        } else {
            // 验证失败
            redisTemplate.delete("token_" + token);
            redisTemplate.opsForValue().set("token_" + token, "failure", 60 * 10, TimeUnit.SECONDS);
            JSONObject data = new JSONObject();
            data.put("status", "failure");
            data.put("version", gtSdk.getVersionInfo());
            return data.toJSONString();
        }
    }

    private static String httpRequest(String requestUrl) {
        StringBuffer buffer = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.connect();

            //读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer != null ? buffer.toString() : null;
    }
}
