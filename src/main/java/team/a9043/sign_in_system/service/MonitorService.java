package team.a9043.sign_in_system.service;

import org.json.JSONObject;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.entity.*;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.repository.SisCourseRepository;
import team.a9043.sign_in_system.repository.SisMonitorTransRepository;
import team.a9043.sign_in_system.repository.SisScheduleRepository;
import team.a9043.sign_in_system.repository.SisSupervisionRepository;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;
import team.a9043.sign_in_system.util.judgetime.ScheduleParserException;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
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
    @Resource
    private SisMonitorTransRepository sisMonitorTransRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("ConstantConditions")
    @Transactional
    public JSONObject getCourses(@NotNull SisUser sisUser) {
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

    @Transactional
    public JSONObject getSupervisions(@NotNull SisUser sisUser,
                                      @NotNull String scId) throws IncorrectParameterException, InvalidPermissionException {

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
                    .forEach(sisSupervision -> sisSupervision.getSsvId().setSisSchedule(null));
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

    @Transactional
    public JSONObject modifyMonitor(@NotNull SisUser sisUser,
                                    @NotNull String scId) throws IncorrectParameterException, InvalidPermissionException {
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

    // TODO async
    @Transactional
    public JSONObject insertSupervision(@NotNull SisUser sisUser,
                                        @NotNull Integer ssId,
                                        @NotNull SisSupervision sisSupervision,
                                        @NotNull LocalDateTime localDateTime) throws IncorrectParameterException, ScheduleParserException, InvalidPermissionException, InvalidTimeParameterException {

        if (sisSupervisionRepository
            .findById(sisSupervision.getSsvId())
            .isPresent()) {
            throw new InvalidPermissionException("Supervision exist: " + ssId + ", " + sisSupervision.getSsvId().getSsvWeek());
        }

        JSONObject jsonObject = new JSONObject();

        SisSchedule sisSchedule = sisScheduleRepository
            .findById(ssId)
            .orElseThrow(() -> new IncorrectParameterException("No ssId: " + ssId));

        SisUser stdSisUser = sisSchedule.getSisCourse().getMonitor();
        if (null == stdSisUser || !sisUser.getSuId().equals(stdSisUser.getSuId())) {
            SisMonitorTrans.IdClass idClass = new SisMonitorTrans.IdClass();
            idClass.setSisSchedule(sisSchedule);
            idClass.setSmtWeek(sisSupervision.getSsvId().getSsvWeek());
            sisMonitorTransRepository
                .findById(idClass)
                .filter(sisMonitorTrans ->
                    sisMonitorTrans.getSmtAgree().equals(SisMonitorTrans.SmtAgree.AGREE) &&
                        sisMonitorTrans.getSisUser().getSuId().equals(sisUser.getSuId()))
                .orElseThrow(() ->
                    new InvalidPermissionException("No permission: " + sisSchedule.getSsId()));
        }

        if (!JudgeTimeUtil.isCourseTime(sisSchedule,
            sisSupervision.getSsvId().getSsvWeek(),
            localDateTime)) {
            jsonObject.put("success", false);
            jsonObject.put("message", "Incorrect time");
            return jsonObject;
        }

        SisSupervision.IdClass idClass = new SisSupervision.IdClass();
        idClass.setSisSchedule(sisSchedule);
        idClass.setSsvWeek(sisSupervision.getSsvId().getSsvWeek());
        sisSupervision.setSsvId(idClass);

        sisSupervisionRepository.saveAndFlush(sisSupervision);
        jsonObject.put("success", true);
        return jsonObject;
    }

    // TODO async
    @Transactional
    public JSONObject applyForTransfer(@NotNull SisUser sisUser,
                                       @NotNull Integer ssId,
                                       @NotNull SisMonitorTrans sisMonitorTrans) throws InvalidPermissionException, IncorrectParameterException {
        SisSchedule sisSchedule = sisScheduleRepository
            .findById(ssId)
            .orElseThrow(() ->
                new IncorrectParameterException("Incorrect ssId" + ssId));

        sisMonitorTrans.getSmtId().setSisSchedule(sisSchedule);
        if (sisMonitorTransRepository
            .findById(sisMonitorTrans.getSmtId())
            .isPresent()) {
            throw new InvalidPermissionException("MonitorTrans exist: " + new JSONObject(sisMonitorTrans.getSmtId()).toString());
        }

        Optional
            .ofNullable(sisSchedule.getSisCourse().getMonitor())
            .filter(monitor -> monitor.getSuId().equals(sisUser.getSuId()))
            .orElseThrow(() ->
                new InvalidPermissionException("Invalid Permission: ssId " + sisSchedule.getSsId()));

        sisMonitorTrans.setSmtAgree(SisMonitorTrans.SmtAgree.UNTREATED);
        sisMonitorTransRepository.saveAndFlush(sisMonitorTrans);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        return jsonObject;
    }

    @Transactional
    public JSONObject modifyTransfer(@NotNull SisUser sisUser,
                                     @NotNull Integer ssId,
                                     @NotNull SisMonitorTrans sisMonitorTrans) throws IncorrectParameterException, InvalidPermissionException {
        SisSchedule sisSchedule = new SisSchedule();
        sisSchedule.setSsId(ssId);
        sisMonitorTrans.getSmtId().setSisSchedule(sisSchedule);

        SisMonitorTrans stdSisMonitorTrans = sisMonitorTransRepository
            .findById(sisMonitorTrans.getSmtId())
            .orElseThrow(() ->
                new IncorrectParameterException("Incorrect sisMonitorTrans: " + sisMonitorTrans.getSmtId().toString()));

        if (!sisMonitorTrans.getSisUser().getSuId().equals(sisUser.getSuId()))
            throw new InvalidPermissionException(
                "Invalid Permission: sisMonitorTrans " + sisMonitorTrans.getSmtId().toString());

        stdSisMonitorTrans.setSmtAgree(sisMonitorTrans.getSmtAgree());
        sisMonitorTransRepository.saveAndFlush(sisMonitorTrans);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        return jsonObject;
    }
}

