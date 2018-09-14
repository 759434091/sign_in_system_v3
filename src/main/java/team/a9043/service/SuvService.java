package team.a9043.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.pojo.*;

import java.time.LocalDateTime;
import java.util.List;

public interface SuvService {
    /**
     * 获得所有督导课程
     *
     * @param userId 用户id
     * @return 督导课程列表
     */
    List<SuvSch> showSuvCourses(String userId);

    List<SuvSch> findToBeSupervised();

    /**
     * 获得正督导课程
     *
     * @param userId        用户id
     * @param localDateTime 用户时间
     * @return 返回督导课程
     */
    SuvSch findOneSuvCoz(String userId, LocalDateTime localDateTime);

    /**
     * 插入督导记录
     *
     * @param suvRecord 督导记录
     * @return 返回成功与否
     */
    boolean insertSuvRec(SuvRecord suvRecord);

    /**
     * 检查是否已经记录
     *
     * @param suvId 督导id
     * @return 返回是否
     */
    boolean isSuvRec(int suvId);

    boolean isContainOSSS(SuvSch suvSch, List<SuvSch> suvSchList);

    /**
     * 获得请假记录
     *
     * @param userId 用户id
     * @return 返回请假记录
     */
    List<SignInRes> getLeaves(String userId);

    /**
     * 审批请假
     *
     * @param user      用户对象
     * @param signInRes 请假对象
     * @return 返回成功与否
     */
    boolean approveLeave(User user, SignInRes signInRes);

    boolean rejectLeave(User user, SignInRes signInRes);

    /**
     * 查看督导历史记录
     *
     * @param user session 用户
     * @return 返回督导历史记录
     */
    List<HisSuvRecRes> findHisSuvRecRes(User user);

    boolean suvLeave(User user, SuvSch suvSch);

    boolean giveUpPower(User user, SuvSch suvSch);

    SuvMan getSignIn(SuvMan suvMan);

    boolean initManSignIn(SuvMan suvMan);

    boolean initAutoSignIn(SuvMan suvMan);

    boolean closeAutoSignIn(SuvMan suvMan);

    boolean openAutoSignIn(SuvMan suvMan);

    boolean closeManSignIn(SuvMan suvMan);

    boolean openManSignIn(SuvMan suvMan);

    boolean cancelSignIn(SuvMan suvMan);

    String checkPower(SuvMan suvMan);

    boolean receiveSuvSch(SuvSch suvSch);

    boolean applyForTrans(SuvTrans suvTrans);

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    boolean acceptSuvTrans(User user, SuvTrans suvTrans);

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    boolean refuseSuvTrans(SuvTrans suvTrans);

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    List<SuvTrans> getSuvTrans(User user);
}
