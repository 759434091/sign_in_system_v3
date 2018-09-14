package team.a9043.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import team.a9043.pojo.*;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SuvMapper {

    /**
     * 查询所有监督课程记录
     *
     * @param userId 用户id
     * @return 返回记录
     */
    List<SuvSch> showSuvCourses(@Param("userId") String userId);

    /**
     * 查询次周正监督课程
     *
     * @param userId  用户id
     * @param suvWeek 第几周
     * @return 返回课程记录
     */
    List<SuvSch> findOneSuvCozList(@Param("userId") String userId, @Param("suvWeek") int suvWeek);

    List<SuvSch> findToBeSupervised();

    /**
     * 查询请假列表
     *
     * @param userId 用户id
     * @return 返回请假列表
     */
    List<SignInRes> getLeaves(@Param("userId") String userId);

    /**
     * 查询是否已经督导记录
     *
     * @param suvId 督导id
     * @return 返回是否
     */
    int isSuvRec(@Param("suvId") int suvId);

    /**
     * 查看督导历史记录
     *
     * @param userId 用户id
     * @return 返回督导历史记录
     */
    List<HisSuvRecRes> findHisSuvRecRes(@Param("userId") String userId);

    List<SuvTrans> getSuvTrans(@Param("userId") String userId);

    /**
     * 插入督导记录
     *
     * @param suvRecord 督导记录
     * @return 返回成功与否
     */
    boolean insertSuvRec(SuvRecord suvRecord);

    /**
     * 审批请假记录
     *
     * @param siId 请假id
     * @return 返回成功与否
     */
    boolean approveLeave(@Param("siId") int siId);

    boolean rejectLeave(@Param("siId") int siId);

    /**
     * 督导员请假
     *
     * @param schId   排课id
     * @param suvWeek 排课周
     * @return 返回成功与否
     */
    boolean suvLeave(@Param("schId") int schId, @Param("suvWeek") int suvWeek, @Param("userId") String userId);

    boolean applyForTrans(SuvTrans suvTrans);

    boolean acceptSuvTrans_1(SuvTrans suvTrans);

    boolean acceptSuvTrans_2(@Param("sutrId") int sutrId);

    boolean refuseSuvTrans(@Param("sutrId") int sutrId);

    /**
     * 督导员放弃权利
     *
     * @return 返回成功与否
     */
    boolean giveUpPower(SuvSch suvSch);

    boolean initManSignIn(SuvMan suvMan);

    boolean initAutoSignIn(SuvMan suvMan);

    SuvMan getSignIn(SuvMan suvMan);

    boolean closeAutoSignIn(SuvMan suvMan);

    boolean openAutoSignIn(SuvMan suvMan);

    boolean closeManSignIn(SuvMan suvMan);

    boolean openManSignIn(SuvMan suvMan);

    boolean deleteSignIn(SuvMan suvMan);

    boolean receiveSuvSch(SuvSch suvSch);

    String checkPower(SuvMan suvMan);
}
