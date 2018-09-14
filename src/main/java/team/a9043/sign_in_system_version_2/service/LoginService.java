package team.a9043.sign_in_system_version_2.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system_version_2.exception.TokenErrorException;
import team.a9043.sign_in_system_version_2.exception.WxNetworkException;
import team.a9043.sign_in_system_version_2.mapper.UserMapper;
import team.a9043.sign_in_system_version_2.pojo.User;
import team.a9043.sign_in_system_version_2.pojo.UserExample;
import team.a9043.sign_in_system_version_2.util.TransSchedule;
import team.a9043.sign_in_system_version_2.util.UserPasswordEncrypt;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("loginService")
public class LoginService {
    private static final String API_URL_STR = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    private static final String APP_ID = "wx5899a00bc0df64a8";
    private static final String SECRET = "b801fd1c57c927a857b4d251b3eebee0";

    @Resource
    private UserMapper userMapper;
    private final TransSchedule transSchedule;
    private final RedisTemplate redisTemplate;

    @Autowired
    public LoginService(TransSchedule transSchedule, RedisTemplate redisTemplate) {
        this.transSchedule = transSchedule;
        this.redisTemplate = redisTemplate;
    }

    public int getCurWeek(LocalDateTime currentDateTime) {
        return transSchedule.getWeek(currentDateTime);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public JSONObject userLogin(Map<String, String> map, String token) throws WxNetworkException, JSONException {
        //redisTemplate.opsForValue().set("token_" + token, "success", 60 * 10, TimeUnit.SECONDS)

        //错误检测
        if (!redisTemplate.hasKey("token_" + token)) {
            redisTemplate.delete("token_" + token);
            throw new TokenErrorException("Token " + token + " is an illegal token!");
        }
        if (redisTemplate.opsForValue().get("token_" + token).toString().equals("failed")) {
            redisTemplate.delete("token_" + token);
            throw new TokenErrorException("Token " + token + " has already invalidated!");
        }
        if (!redisTemplate.opsForValue().get("token_" + token).toString().equals("success")) {
            throw new TokenErrorException("Token " + token + " has already logged in!");
        }

        return Optional
                .ofNullable(userMapper.selectByPrimaryKey(map.get("usrId")))
                .filter(stdUser -> stdUser.getUsrPwd().equals(UserPasswordEncrypt.encrypt(map.get("usrPwd"))))
                .map(stdUser -> {
                    //若请求绑定
                    JSONObject jsonObject = new JSONObject();
                    boolean isBind = Optional.ofNullable(map.get("code")).filter(code -> !code.equals(""))
                            .map((code) -> {
                                JSONObject wxObj = new JSONObject(Optional
                                        .ofNullable(httpRequest(String.format(API_URL_STR, APP_ID, SECRET, code)))
                                        .orElseThrow(() -> new WxNetworkException("can not connect to wx server")));

                                return Optional.ofNullable(wxObj.getString("openid"))
                                        .map(opId -> {
                                            stdUser.setOpenId(opId);
                                            return userMapper.updateByPrimaryKeySelective(stdUser) > 0;
                                        })
                                        .orElseThrow(() -> new WxNetworkException("can not connect to wx server"));
                            })
                            .orElse(false);

                    //创建登录凭证
                    redisTemplate.opsForValue().set("token_" + token, stdUser, 20, TimeUnit.MINUTES);
                    //对外隐藏密码
                    stdUser.setUsrPwd(null);
                    jsonObject.put("user", new JSONObject(stdUser));
                    jsonObject.put("isBind", isBind);
                    jsonObject.put("token", token);
                    return jsonObject;
                })
                .orElseGet(() -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msg", "用户名或密码错误");
                    jsonObject.put("err", -1);
                    return jsonObject;
                });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public JSONObject wxLogin(Map<String, String> map) throws JSONException {
        JSONObject wxUserInfo = new JSONObject(Objects.requireNonNull(httpRequest(String.format(API_URL_STR, APP_ID, SECRET, map.get("code")))));
        String openId = wxUserInfo.getString("openid");

        //openid err
        if (openId == null || openId.equals("")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("err", "can not get openid");
            return jsonObject;
        }

        //no user
        UserExample userExample = new UserExample();
        userExample.createCriteria().andOpenIdEqualTo(openId);
        List<User> userList = userMapper.selectByExample(userExample);
        if (userList.size() == 0) {
            String token = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set("token_" + token, "success",  10, TimeUnit.MINUTES);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("isBind", false);
            jsonObject.put("token", token);
            return jsonObject;
        }

        //login
        User user = userList.get(0);
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("token_" + token, user, 20, TimeUnit.MINUTES);
        user.setUsrPwd(null);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", new JSONObject(user));
        jsonObject.put("isBind", true);
        jsonObject.put("token", token);
        return jsonObject;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean changePassword(User user, String newPwd) {
        //保护不修改数据
        user.setUsrName(null);
        user.setUsrPermit(null);
        user.setOpenId(null);
        //修改密码
        user.setUsrPwd(newPwd);
        return userMapper.updateByPrimaryKeySelective(user) > 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean bindOpenId(User user) {
        return userMapper.updateByPrimaryKeySelective(user) > 0;
    }

    /**
     * 封装的简单httpRequest get模块
     *
     * @param requestUrl get URL
     * @return 返回response
     */
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
