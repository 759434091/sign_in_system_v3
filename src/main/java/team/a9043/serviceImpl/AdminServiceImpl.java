package team.a9043.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.mapper.AdminMapper;
import team.a9043.mapper.CourseMapper;
import team.a9043.mapper.SuvMapper;
import team.a9043.mapper.TchMapper;
import team.a9043.pojo.*;
import team.a9043.service.AdminService;

import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    private final static HashMap<String, String> COZ_TABLE_MAP = getCourseTableMap();

    private AdminMapper adminMapper;

    private CourseMapper courseMapper;

    private TchMapper tchMapper;

    private SuvMapper suvMapper;

    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper, CourseMapper courseMapper, TchMapper tchMapper, SuvMapper suvMapper) {
        this.adminMapper = adminMapper;
        this.courseMapper = courseMapper;
        this.tchMapper = tchMapper;
        this.suvMapper = suvMapper;
    }

    private static HashMap<String, String> getCourseTableMap() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("courseTableMap");
        HashMap<String, String> hashMap = new HashMap<>();
        for (String key : resourceBundle.keySet()) {
            hashMap.put(key, resourceBundle.getString(key));
        }
        return hashMap;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<User> findSuv() {
        return adminMapper.findSuv();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SuvSch> findSuvSch(User user) {
        return adminMapper.findSuvSch(user.getUserId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public int getAllCourseNumber(String cozName, String cozDepart, Integer cozGrade) {
        return adminMapper.getAllCourseNumber(cozName, cozDepart, cozGrade);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<AdmCourse> showAllCourses(String cozName, String cozDepart, Integer cozGrade, int page, int pageSize, String sortStr, boolean isAsc) {
        List<AdmCourse> courseList = adminMapper.showAllCourses((page - 1) * pageSize, pageSize, COZ_TABLE_MAP.get(sortStr), isAsc, cozName, cozDepart, cozGrade);
        List<String> cozIdList = new LinkedList<>();
        List<Schedule> scheduleList;
        for (AdmCourse course : courseList) {
            cozIdList.add(course.getCozId());
        }
        scheduleList = adminMapper.showAllCourses_getSch(cozIdList);
        for (AdmCourse course : courseList) {
            List<Schedule> oneCozSchList = new LinkedList<>();
            for (Schedule schedule : scheduleList) {
                if (schedule.getCozId().equals(course.getCozId())) {
                    oneCozSchList.add(schedule);
                }
            }
            course.setSchedules(oneCozSchList);
        }
        return courseList;
    }

    @Override
    public List<User> getCozStudent(String cozId) {
        return tchMapper.getCozStudent(cozId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<User> showOneCozStuList(Course course) {
        return adminMapper.showOneCozStuList(course.getCozId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public OneSchSuvRec showOneSchRec(Schedule schedule) {
        return adminMapper.showOneSchRec(schedule.getSchId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public OneStuSchedule showCourses(User user) {
        return courseMapper.showCourses(user.getUserId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SuvSch> showSuvCourses(String userId) {
        List<SuvSch> suvSchList = suvMapper.showSuvCourses(userId);
        for (SuvSch suvSch : suvSchList) {
            if (suvSch.getSuvMan().getSuvManId() == 0) {
                suvSch.setSuvMan(null);
            }
            suvSch.getCourse().setSchedules(null);
        }
        return suvSchList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<HisSuvRecRes> findHisSuvRecRes(String userId) {
        return adminMapper.findHisSuvRecRes(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<HisSuvRecRes> findHisAllRecRes(String userId) {
        return adminMapper.findHisAllRecRes(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean grantSuv(User user) {
        return adminMapper.grantSuv(user.getUserId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean revokeSuv(User user) {
        return adminMapper.revokeSuv(user.getUserId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SchAbsRec> fSchAbsRecByCoz(String cozId) {
        return adminMapper.fSchAbsRecByCoz(cozId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public int updateCozAttRate() {
        int row = 0;
        List<CozAttRate> cozAttRateList = adminMapper.getCozAttRate();
        for (CozAttRate cozAttRate : cozAttRateList) {
            row += adminMapper.insertCozAttRate(cozAttRate);
        }
        return row;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public int updateCozNumber() {
        int row = 0;
        List<CozNumber> cozNumberList = adminMapper.getCozNumber();
        for (CozNumber cozNumber : cozNumberList) {
            row += adminMapper.insertCozNumber(cozNumber);
        }
        return row;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<Schedule> findScheduleByTime(int schTime) {
        return adminMapper.findScheduleByTime(schTime);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public SuvMan findSuvMan(int schId, int siWeek) {
        return adminMapper.findSuvMan(schId, siWeek);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<Integer> findCozSuv(Schedule schedule) {
        return tchMapper.findCozSuv(schedule.getSchId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean setCozSuv(SuvSch suvSch) {
        tchMapper.removeCozSuv(suvSch);
        return tchMapper.setCozSuv(suvSch);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean removeCozSuv(SuvSch suvSch) {
        return tchMapper.removeCozSuv(suvSch);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean setCozSignIn(SuvMan suvMan) {
        tchMapper.removeCozSignIn(suvMan);
        return tchMapper.setCozSignIn(suvMan);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<Integer> getCozSignIn(Schedule schedule) {
        return tchMapper.getCozSignIn(schedule.getSchId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean removeCozSignIn(SuvMan suvMan) {
        return tchMapper.removeCozSignIn(suvMan);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SignInRes> getLeaves(String cozId) {
        List<SignInRes> signInResList = adminMapper.getLeaves(cozId);
        for (SignInRes signInRes : signInResList) {
            signInRes.getOneCozAndSch().getCourse().setSchedules(null);
        }
        return signInResList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean approveLeave(SignInRes signInRes) {
        return suvMapper.approveLeave(signInRes.getSiId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean rejectLeave(SignInRes signInRes) {
        return suvMapper.rejectLeave(signInRes.getSiId());
    }

    @Override
    public JSONObject selectAbcStatistics(Integer week, Integer page, String cozName, String cozDepart, Integer cozGrade, String userId, String userName) {
        List<AbsStatistics> absStatisticsList1 = adminMapper.selectAbcStatistics(week, cozName, cozDepart, cozGrade, userId, userName);
        List<AbsStatistics> absStatisticsList2 = adminMapper.selectAbcStatisticsNeverSignIn(week, cozName, cozDepart, cozGrade, userId, userName);
        for (AbsStatistics absStatistics : absStatisticsList1) {
            int schId1 = absStatistics.getSchedule().getSchId();
            int week1 = absStatistics.getSarWeek();
            for (int j = 0; j < absStatisticsList2.size(); j++) {
                int schId2 = absStatisticsList2.get(j).getSchedule().getSchId();
                int week2 = absStatisticsList2.get(j).getSarWeek();
                if (schId1 == schId2 && week1 == week2) {
                    Set<User> userSet = new HashSet<>(absStatistics.getStudentList()); //list1转set
                    userSet.addAll(absStatisticsList2.get(j).getStudentList()); //list2并入
                    absStatistics.setStudentList(new ArrayList<>(userSet));//set 转 list
                    absStatisticsList2.remove(j); //不会出错,会跳出循环
                    break;
                }
            }
        }
        //list1&list2相同的排课已经并入, 且从list2 删除
        absStatisticsList1.addAll(absStatisticsList2);
        //计算总条数
        int totalNum = 0;
        for (AbsStatistics absStatistics : absStatisticsList1) {
            totalNum += absStatistics.getStudentList().size();
        }
        //10为每页条数
        int num = 10;
        //offset
        int offset = page * num;
        boolean isBreak = false;
        for (int i = 0; i < absStatisticsList1.size(); i++) {
            if (isBreak) {
                break;
            }
            AbsStatistics absStatistics = absStatisticsList1.get(i);
            for (int j = 0; j < absStatistics.getStudentList().size(); j++) {
                if (offset == 0) {
                    absStatistics.setStudentList(absStatistics.getStudentList().subList(j, absStatistics.getStudentList().size()));
                    absStatisticsList1 = absStatisticsList1.subList(i, absStatisticsList1.size());
                    isBreak = true;
                    break;
                } else {
                    offset--;
                }
            }
        }
        //page
        for (int i = 0; i < absStatisticsList1.size(); i++) {
            AbsStatistics absStatistics = absStatisticsList1.get(i);
            for (int j = 0; j < absStatistics.getStudentList().size(); j++) {
                if (num == 0) {
                    absStatistics.setStudentList(absStatistics.getStudentList().subList(0, j));
                    absStatisticsList1 = absStatisticsList1.subList(0, i + 1);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("list", absStatisticsList1);
                    jsonObject.put("totalNum", totalNum);
                    return jsonObject;
                } else {
                    num--;
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list", absStatisticsList1);
        jsonObject.put("totalNum", totalNum);
        return jsonObject;
    }
}
