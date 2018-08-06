package team.a9043.sign_in_system.service;

import lombok.Getter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.entity.SisCourse;
import team.a9043.sign_in_system.entity.SisJoinCourse;
import team.a9043.sign_in_system.entity.SisSchedule;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.repository.SisCourseRepository;
import team.a9043.sign_in_system.repository.SisJoinCourseRepository;
import team.a9043.sign_in_system.repository.SisScheduleRepository;
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
    private SisScheduleRepository sisScheduleRepository;
    @Resource
    private SisJoinCourseRepository sisJoinCourseRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Transactional
    public JSONObject getCourses(@Nullable Integer page) {
        @Getter
        class StreamIntegration {
            private StreamIntegration(Collection<SisSchedule> sisSchedules,
                                      Collection<SisJoinCourse> sisJoinCourseList) {
                this.sisSchedules = sisSchedules;
                this.sisJoinCourseList = sisJoinCourseList;
            }

            Collection<SisSchedule> sisSchedules;
            Collection<SisJoinCourse> sisJoinCourseList;
        }
        page = null == page ? 0 : page;
        Page<SisCourse> sisCoursePage = sisCourseRepository
            .findAll(PageRequest.of(page, coursePageSize));

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
            .map(sisCourse -> new StreamIntegration(sisCourse.getSisSchedules(), sisCourse.getSisJoinCourseList()))
            .forEach(streamIntegration -> {
                streamIntegration.getSisSchedules()
                    .forEach(sisSchedule -> {
                        sisSchedule.getSisSupervisions();
                        Optional
                            .ofNullable(sisSchedule.getMonitor())
                            .ifPresent(monitor -> {
                                monitor.setSuPassword(null);
                                monitor.setSisSchedules(null);
                                monitor.setSisJoinCourses(null);
                            });
                        sisSchedule.setSisCourse(null);
                    });
                streamIntegration.getSisJoinCourseList()
                    .forEach(sisJoinCourse -> {
                        Optional
                            .ofNullable(sisJoinCourse.getSisUser())
                            .ifPresent(sisUser -> {
                                sisUser.setSuPassword(null);
                                sisUser.setSisJoinCourses(null);
                                sisUser.setSisSchedules(null);
                            });
                        sisJoinCourse.setSisCourse(null);
                    });
            });

        entityManager.clear();
        jsonObject.put("success", true);
        jsonObject.put("page", page);
        jsonObject.put("array", sisCourseList);
        return jsonObject;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
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
                sisCourse
                    .getSisSchedules()
                    .forEach(sisSchedule -> sisSchedule.setSisCourse(null));
                sisCourse.setSisJoinCourseList(sisCourse
                    .getSisJoinCourseList()
                    .stream()
                    .filter(tSisJoinCourse -> tSisJoinCourse.getJoinCourseType().equals(SisJoinCourse.JoinCourseType.TEACHING))
                    .peek(tSisJoinCourse -> {
                        SisUser sisUser1 = tSisJoinCourse.getSisUser();
                        sisUser1.setSisJoinCourses(null);
                        sisUser1.setSuPassword(null);
                        tSisJoinCourse.setSisCourse(null);
                    })
                    .collect(Collectors.toList()));
            });

        entityManager.clear();
        jsonObject.put("success", true);
        jsonObject.put("array", sisJoinCourses);
        return jsonObject;
    }

    @Transactional
    public JSONObject setSsNeedMonitor(SisSchedule sisSchedule) {
        SisSchedule updateSchedule = new SisSchedule();
        updateSchedule.setSsId(sisSchedule.getSsId());
        updateSchedule.setSsNeedMonitor(true);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            null != sisScheduleRepository.save(updateSchedule));
        return jsonObject;
    }
}
