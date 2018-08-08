package team.a9043.sign_in_system.service;

import org.json.JSONObject;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.entity.SisCourse;
import team.a9043.sign_in_system.entity.SisSchedule;
import team.a9043.sign_in_system.entity.SisSupervision;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.repository.SisCourseRepository;
import team.a9043.sign_in_system.repository.SisScheduleRepository;
import team.a9043.sign_in_system.repository.SisSupervisionRepository;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;
import team.a9043.sign_in_system.util.judgetime.ScheduleParerException;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

/**
 * @author a9043
 */
@Service
public class MonitorService {
    @Resource
    private SisCourseRepository sisCourseRepository;
    @Resource
    private SisScheduleRepository sisScheduleRepository;
    @Resource
    private SisSupervisionRepository sisSupervisionRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("ConstantConditions")
    @Transactional
    public JSONObject getCourses(SisUser sisUser) {
        SisCourse sisCourse = new SisCourse();
        sisCourse.setMonitor(sisUser);

        Collection<SisCourse> sisCourses = sisCourseRepository
            .findAll(Example.of(sisCourse));

        sisCourses
            .parallelStream()
            .forEach(stdSisCourse -> {
                stdSisCourse.setMonitor(null);

                stdSisCourse
                    .getSisJoinCourseList()
                    .forEach(sisJoinCourse -> {
                        sisJoinCourse.setSisCourse(null);
                        SisUser tSisUser = sisJoinCourse.getSisUser();
                        tSisUser.setSisJoinCourses(null);
                        tSisUser.setSisSchedules(null);
                        tSisUser.setSuPassword(null);
                    });

                stdSisCourse
                    .getSisSchedules()
                    .forEach(sisSchedule -> {
                        sisSchedule.setSisCourse(null);
                        sisSchedule.setSisSupervisions(null);
                    });
            });

        entityManager.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("array", sisCourses);
        jsonObject.put("arrSize", sisCourses.size());
        return jsonObject;
    }

    public JSONObject modifyMonitor(SisUser sisUser, String scId) throws IncorrectParameterException, InvalidPermissionException {
        SisCourse sisCourse = sisCourseRepository
            .findById(scId)
            .orElseThrow(() -> new IncorrectParameterException("No course: " + scId));

        if (null != sisCourse.getMonitor()) {
            throw new InvalidPermissionException("Invalid permission: " + scId);
        }

        sisCourse.setMonitor(sisUser);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", null != sisCourseRepository.save(sisCourse));
        return jsonObject;
    }

    @Transactional
    public JSONObject insertSupervision(SisUser sisUser,
                                        Integer ssId,
                                        SisSupervision sisSupervision,
                                        LocalDateTime localDateTime) throws
        IncorrectParameterException,
        ScheduleParerException,
        InvalidPermissionException {

        JSONObject jsonObject = new JSONObject();

        SisSchedule sisSchedule = sisScheduleRepository
            .findById(ssId)
            .orElseThrow(() -> new IncorrectParameterException("No ssId: " + ssId));

        SisUser stdSisUser = sisSchedule.getSisCourse().getMonitor();
        if (null == stdSisUser || !sisUser.getSuId().equals(stdSisUser.getSuId())) {
            throw new InvalidPermissionException("No permission: " + sisSchedule.getSsId());
        }

        if (!JudgeTimeUtil.isCourseTime(sisSchedule,
            sisSupervision.getSsvWeek(),
            localDateTime)) {
            jsonObject.put("success", false);
            jsonObject.put("message", "Incorrect time");
            return jsonObject;
        }

        sisSupervision.(sisSchedule);
        jsonObject.put("success",
            null == sisSupervisionRepository.save(sisSupervision));
        return jsonObject;
    }

    @Transactional
    public JSONObject getSupervisions(SisUser sisUser, String scId) throws
        IncorrectParameterException, InvalidPermissionException {

        SisCourse sisCourse = sisCourseRepository
            .findById(scId)
            .orElseThrow(() -> new IncorrectParameterException("No course: " + scId));

        Optional.of(sisCourse.getMonitor())
            .filter(monitor -> monitor.getSuId().equals(sisUser.getSuId()))
            .orElseThrow(() -> new InvalidPermissionException(
                "Invalid permission: " + scId));

        Collection<SisSchedule> sisSchedules = sisCourse.getSisSchedules();
        sisSchedules
            .forEach(sisSchedule -> {
                sisSchedule
                    .getSisSupervisions()
                    .forEach(sisSupervision -> sisSupervision.setSisSchedule(null));
                sisSchedule.setSisCourse(null);
            });

        entityManager.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("array", sisSchedules);
        jsonObject.put("arrSize", sisSchedules.size());
        return jsonObject;
    }
}

