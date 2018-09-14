package team.a9043.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;
import team.a9043.pojo.Location;
import team.a9043.pojo.Schedule;
import team.a9043.pojo.SuvMan;

import java.time.LocalDateTime;

@Repository
public interface SignInMapper {
    /**
     * 获得地点对象
     *
     * @param locName 地点名字
     * @return 返回地点对象
     */
    Location fOneLocation(@Param("locName") String locName);

    /**
     * 检查是否已经签到
     *
     * @param schId  排课id
     * @param siWeek 第几周
     * @return 返回是否
     */
    int isSignIn(@Param("userId") String userId, @Param("schId") int schId, @Param("siWeek") int siWeek);

    /**
     * 检查是否已经发起人工签到
     *
     * @param schId  排课id
     * @param siWeek 第几周
     * @return 返回是否
     */
    SuvMan findSuvMan(@Param("schId") int schId, @Param("siWeek") int siWeek);

    /**
     * 插入签到记录
     *
     * @param userId 用户id
     * @param schId  排课id
     * @param siTime 签到时间
     * @param siWeek 第几周
     * @return 返回成功与否
     */
    boolean insertSignIn(@Param("userId") String userId, @Param("schId") int schId, @Param("siTime") LocalDateTime siTime, @Param("siWeek") int siWeek);

    /**
     * 插入请假记录
     *
     * @param userId 用户id
     * @param schId  排课id
     * @param siTime 签到时间
     * @param siWeek 第几周
     * @return 返回成功与否
     */
    boolean insertLeave(@Param("userId") String userId, @Param("schId") int schId, @Param("siTime") LocalDateTime siTime, @Param("siWeek") int siWeek, @Param("siVoucher") byte[] siVoucher);

    Schedule findSchedule(@Param("schId") int schId);
}
