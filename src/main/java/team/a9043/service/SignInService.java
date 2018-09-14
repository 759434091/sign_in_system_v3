package team.a9043.service;

import org.springframework.web.multipart.MultipartFile;
import team.a9043.pojo.Location;
import team.a9043.pojo.Schedule;
import team.a9043.pojo.User;

import java.time.LocalDateTime;

/**
 * 签到服务
 */
public interface SignInService {
    /**
     * 签到
     *
     * @param student       用户对象
     * @param localDateTime 用户时间
     * @return 签到成功与否
     */
    String getSignInRes(User student, Schedule schedule, LocalDateTime localDateTime);

    /**
     * 请假
     *
     * @param student       用户对象
     * @param localDateTime 用户时间
     * @return 签到成功与豆
     */
    boolean getLeaveRes(User student, MultipartFile voucher, Schedule schedule, LocalDateTime localDateTime);

    /**
     * 检查已经签到
     *
     * @param localDateTime 用户时间
     * @return 返回是否
     */
    boolean checkSignIn(User user, Schedule schedule, LocalDateTime localDateTime);

    boolean isSignIn(String userId, int schId, int siWeek);
}
