package team.a9043.sign_in_system.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.entity.SisCourse;
import team.a9043.sign_in_system.entity.SisSchedule;
import team.a9043.sign_in_system.entity.SisSupervision;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
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
    private SisScheduleRepository sisScheduleRepository;
    @Resource
    private SisSupervisionRepository sisSupervisionRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("ConstantConditions")
    @Transactional
    public JSONObject getCourses(SisUser sisUser) {
        SisSchedule tSisSchedule = new SisSchedule();
        tSisSchedule.setMonitor(sisUser);

        Collection<SisSchedule> sisSchedules = sisScheduleRepository
            .findAll(Example.of(tSisSchedule));

        sisSchedules
            .parallelStream()
            .forEach(sisSchedule -> {
                sisSchedule.setMonitor(null);

                SisCourse sisCourse = sisSchedule.getSisCourse();
                sisCourse
                    .getSisJoinCourseList()
                    .forEach(sisJoinCourse -> {
                        sisJoinCourse.setSisCourse(null);

                        SisUser tSisUser = sisJoinCourse.getSisUser();
                        tSisUser.setSisJoinCourses(null);
                        tSisUser.setSisSchedules(null);
                        tSisUser.setSuPassword(null);
                    });
                sisCourse.setSisSchedules(null);
            });

        entityManager.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("array", new JSONArray(sisSchedules));
        jsonObject.put("arrSize", sisSchedules.size());
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

        if (!sisUser.getSuId().equals(sisSchedule.getMonitor().getSuId())) {
            throw new InvalidPermissionException("No permission: " + sisSchedule.getSsId());
        }

        if (!JudgeTimeUtil.isCourseTime(sisSchedule,
            sisSupervision.getSsvWeek(),
            localDateTime)) {
            jsonObject.put("success", false);
            jsonObject.put("message", "Incorrect time");
            return jsonObject;
        }

        sisSupervision.setSisSchedule(sisSchedule);
        jsonObject.put("success",
            null == sisSupervisionRepository.save(sisSupervision));
        return jsonObject;
    }

    @Transactional
    public JSONObject getSupervisions(SisUser sisUser, Integer ssId) throws
        IncorrectParameterException, InvalidPermissionException {

        SisSchedule stdSisSchedule = sisScheduleRepository
            .findById(ssId)
            .orElseThrow(() -> new IncorrectParameterException("No ssId: " + ssId));

        Collection<SisSupervision> sisSupervisions = Optional
            .of(stdSisSchedule)
            .filter(sisSchedule -> sisSchedule.getMonitor().getSuId().equals(sisUser.getSuId()))
            .map(sisSchedule -> {
                Collection<SisSupervision> stdSisSupervisions =
                    sisSchedule.getSisSupervisions();
                stdSisSupervisions.
                    forEach(sisSupervision -> sisSupervision.setSisSchedule(null));
                return stdSisSupervisions;
            })
            .orElseThrow(() -> new InvalidPermissionException(
                "Invalid permission: " + ssId));

        entityManager.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("array", new JSONArray(sisSupervisions));
        jsonObject.put("arrSize", sisSupervisions.size());
        return jsonObject;
    }
}

