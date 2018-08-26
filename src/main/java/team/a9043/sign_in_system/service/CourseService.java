package team.a9043.sign_in_system.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.entity.SisCourse;
import team.a9043.sign_in_system.entity.SisJoinCourse;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.repository.SisCourseRepository;
import team.a9043.sign_in_system.repository.SisJoinCourseRepository;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author a9043
 */
@Service
public class CourseService {
    @Value("${pageSize.coursePageSize}")
    private Integer coursePageSize;
    @Resource
    private SisCourseRepository sisCourseRepository;
    @Resource
    private SisJoinCourseRepository sisJoinCourseRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions",
        "Duplicates"})
    @Transactional
    public JSONObject getCourses(@Nullable Boolean needMonitor,
                                 @Nullable Boolean hasMonitor,
                                 @Nullable Integer page) {
        page = null == page ? 0 : page;

        Page<SisCourse> sisCoursePage;
        if (null != needMonitor) {
            if (null == hasMonitor)
                sisCoursePage = sisCourseRepository.findAllByScNeedMonitorIs(
                    needMonitor,
                    PageRequest.of(page, coursePageSize));
            else if (hasMonitor)
                sisCoursePage =
                    sisCourseRepository.findAllByScNeedMonitorIsAndMonitorNotNull(
                        needMonitor,
                        PageRequest.of(page, coursePageSize));
            else
                sisCoursePage =
                    sisCourseRepository.findAllByScNeedMonitorIsAndMonitorIsNull(
                        needMonitor,
                        PageRequest.of(page, coursePageSize));
        } else {
            sisCoursePage = sisCourseRepository.findAll(
                PageRequest.of(page, coursePageSize));
        }

        JSONObject jsonObject = new JSONObject();
        List<SisCourse> sisCourseList = sisCoursePage.getContent();

        if (sisCourseList.size() <= 0) {
            jsonObject.put("success", false);
            jsonObject.put("page", page);
            jsonObject.put("message", "No courses");
            return jsonObject;
        }

        sisCourseList
            .parallelStream()
            .forEach(sisCourse -> {
                Optional
                    .ofNullable(sisCourse.getMonitor())
                    .ifPresent(monitor -> {
                        monitor.setSuPassword(null);
                        monitor.setSisCourses(null);
                        monitor.setSisJoinCourses(null);
                        monitor.setSisSignInDetails(null);
                    });
                sisCourse.getSisSchedules()
                    .forEach(sisSchedule -> {
                        sisSchedule.getSisSupervisions().forEach(sisSupervision -> {
                            sisSupervision.getSsvId().setSisSchedule(null);
                        });

                        sisSchedule.setSisCourse(null);
                        sisSchedule.setSisSignIns(null);
                    });
                sisCourse.getSisJoinCourseList()
                    .forEach(sisJoinCourse -> {
                        sisJoinCourse.setSisCourse(null);

                        SisUser sisUser = sisJoinCourse.getSisUser();
                        sisUser.setSuPassword(null);
                        sisUser.setSisJoinCourses(null);
                        sisUser.setSisCourses(null);
                        sisUser.setSisSignInDetails(null);
                        sisUser.setSisMonitorTrans(null);
                    });
                Optional.ofNullable(sisCourse.getMonitor())
                    .ifPresent(sisUser -> {
                        sisUser.setSisMonitorTrans(null);
                        sisUser.setSisSignInDetails(null);
                        sisUser.setSisCourses(null);
                        sisUser.setSisJoinCourses(null);
                        sisUser.setSuPassword(null);
                    });
            });

        entityManager.clear();
        jsonObject.put("success", true);
        jsonObject.put("page", page);
        jsonObject.put("array", sisCourseList);
        return jsonObject;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions",
        "Duplicates"})
    @Transactional
    public JSONObject getCourses(@TokenUser SisUser sisUser) {
        SisJoinCourse tempJoinCourse = new SisJoinCourse();
        tempJoinCourse.setSisUser(sisUser);

        Collection<SisJoinCourse> sisJoinCourses = sisJoinCourseRepository
            .findAll(Example.of(tempJoinCourse));

        JSONObject jsonObject = new JSONObject();
        if (sisJoinCourses.size() <= 0) {
            jsonObject.put("success", false);
            jsonObject.put("message", "No courses");
            return jsonObject;
        }

        sisJoinCourses
            .parallelStream()
            .forEach(sisJoinCourse -> {
                sisJoinCourse.setSisUser(null);
                SisCourse sisCourse = sisJoinCourse.getSisCourse();
                sisCourse.setMonitor(null);

                sisCourse
                    .getSisSchedules()
                    .forEach(sisSchedule -> {
                        sisSchedule.setSisCourse(null);
                        sisSchedule.setSisSignIns(null);
                        sisSchedule.setSisSupervisions(null);
                    });

                sisCourse.setSisJoinCourseList(sisCourse
                    .getSisJoinCourseList()
                    .stream()
                    .filter(tSisJoinCourse -> tSisJoinCourse.getJoinCourseType().equals(SisJoinCourse.JoinCourseType.ATTENDANCE))
                    .peek(tSisJoinCourse -> {
                        SisUser sisUser1 = tSisJoinCourse.getSisUser();
                        sisUser1.setSisJoinCourses(null);
                        sisUser1.setSisCourses(null);
                        sisUser1.setSisSignInDetails(null);
                        sisUser1.setSisMonitorTrans(null);
                        sisUser1.setSuPassword(null);
                        tSisJoinCourse.setSisCourse(null);
                    })
                    .collect(Collectors.toList()));

                Optional.ofNullable(sisCourse.getMonitor()).ifPresent(monitor -> {
                    monitor.setSisMonitorTrans(null);
                    monitor.setSisSignInDetails(null);
                    monitor.setSisCourses(null);
                    monitor.setSisJoinCourses(null);
                    monitor.setSuPassword(null);
                });
            });

        entityManager.clear();
        jsonObject.put("success", true);
        jsonObject.put("array", sisJoinCourses);
        return jsonObject;
    }

    @Transactional
    public JSONObject modifyScNeedMonitor(SisCourse sisCourse) throws IncorrectParameterException {
        SisCourse stdSisCourse = sisCourseRepository
            .findById(sisCourse.getScId())
            .map(tSisCourse -> {
                boolean scNeedMonitor = sisCourse.getScNeedMonitor();

                tSisCourse.setScNeedMonitor(scNeedMonitor);
                if (!scNeedMonitor) {
                    tSisCourse.setMonitor(null);
                    return tSisCourse;
                }

                SisUser sisUser = sisCourse.getMonitor();
                if (null != sisUser) {
                    tSisCourse.setMonitor(sisUser);
                }

                return tSisCourse;
            })
            .orElseThrow(() -> new IncorrectParameterException("No course: " + sisCourse.getScId()));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            sisCourseRepository.save(stdSisCourse));
        return jsonObject;
    }
}
