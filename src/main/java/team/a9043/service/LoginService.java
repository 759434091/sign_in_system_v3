package team.a9043.service;

        import team.a9043.pojo.User;

/**
 * 登录服务
 */
public interface LoginService {
    /**
     * 登录
     *
     * @param userId  用户id
     * @param userPwd 用户密码
     * @return 用户对象
     */
    User userLogin(String userId, String userPwd);

    User wxLogin(String openId);

    boolean bindOpenId(String openId, String userId);

    boolean changePassword(String userId, String userPwd);
}
