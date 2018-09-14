package team.a9043.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.JavaUtil.TransSchedule;
import team.a9043.mapper.CourseMapper;
import team.a9043.pojo.*;
import team.a9043.service.CourseService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private CourseMapper courseMapper;

    @Autowired
    public CourseServiceImpl(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    /**
     * 获得一个学生的课表
     *
     * @param userId 用户id
     * @return 一个学生的课表
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public OneStuSchedule showCourses(String userId) {
        return courseMapper.showCourses(userId);
    }

    /**
     * 查询当前正在上的课的信息
     *
     * @param localDateTime 时间
     * @return 当前正在上的课的信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public OneCozAndSch checkCourse(User user, LocalDateTime localDateTime) {
        Schedule tSchedule = TransSchedule.getSchedule(localDateTime);
        OneCozAndSch oneCozAndSch = courseMapper.fSchedule(user.getUserId(), tSchedule);
        if (oneCozAndSch != null) {
            oneCozAndSch.getSchedule().setSchWeek(tSchedule.getSchWeek());
            oneCozAndSch.getCourse().setSchedules(null);
            return oneCozAndSch;
        } else {
            return null;
        }
    }

    /**
     * 查询一个schedule的签到信息
     *
     * @param schedule 当前查询排课
     * @return 签到列表(包括未审批)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SignInRes> fOneCozSignIn(User user, Schedule schedule) {
        return courseMapper.fOneCozSignIn(user.getUserId(), schedule.getSchId());
    }

    /**
     * 查询一个schedule的缺勤信息
     *
     * @param schedule      当前查询排课
     * @param localDateTime 查询当前周
     * @return 缺勤信息(不包括请假未审批以及老师特指缺勤)
     * 序号 到勤
     * -1 缺勤
     * 0 未来周
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public int[] fOneCozAbsent(User user, Schedule schedule, LocalDateTime localDateTime) {
        int[] integerList = new int[TransSchedule.MAX_WEEK]; //预备数组
        List<SignInRes> signInResList = courseMapper.fOneCozSignIn(user.getUserId(), schedule.getSchId()); //预备结果
        List<SuvMan> suvManList = courseMapper.findSuvMan(schedule.getSchId()); //获得需要签到的周数
        int week = TransSchedule.getWeek(localDateTime); //获得当前周
        for (int i = 0; i < week; i++) {
            SuvMan suvMan = null;
            //该周是否签到
            for (SuvMan suvManTemp : suvManList) {
                if (suvManTemp.getSiWeek() == i + 1) {
                    suvMan = suvManTemp;
                }
            }
            //如果需要签到
            if (suvMan != null) {
                integerList[i] = -1;
                for (SignInRes signInRes : signInResList) {
                    if (signInRes.getSiWeek() == suvMan.getSiWeek() && signInRes.getOneCozAndSch().getSchedule().getSchId() == suvMan.getSchId()) {
                        integerList[i] = i + 1;
                    }
                }
            } else {
                integerList[i] = i + 1;
            }
        }
        return integerList;
    }
}
