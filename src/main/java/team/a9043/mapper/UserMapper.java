package team.a9043.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import team.a9043.pojo.User;

@Repository
public interface UserMapper {
    /**
     * 学生账号密码查询
     *
     * @param userId  学生id
     * @param userPwd 学生密码
     * @return 返回用户对象
     */
    User fByStuNameAndPwd(@Param("userId") String userId, @Param("userPwd") String userPwd);

    /**
     * 老师账号密码查询
     *
     * @param userId  老师id
     * @param userPwd 老师密码
     * @return 返回用户对象
     */
    User fByTchNameAndPwd(@Param("userId") String userId, @Param("userPwd") String userPwd);

    User fByStuOpenId_stu(@Param("openId") String openId);

    User fByStuOpenId_tch(@Param("openId") String openId);

    boolean bindOpenId_stu(@Param("openId") String openId, @Param("userId") String userId);

    boolean bindOpenId_tch(@Param("openId") String openId, @Param("userId") String userId);

    boolean changeStuPassword(@Param("userId") String userId, @Param("userPwd") String userPwd);

    boolean changeTchPassword(@Param("userId") String userId, @Param("userPwd") String userPwd);
}
