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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    // TODO trans
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
                        tSisUser.setSisSignInDetails(null);
                        tSisUser.setSisMonitorTrans(null);
                        tSisUser.setSisJoinCourses(null);
                        tSisUser.setSisCourses(null);
                        tSisUser.setSuPassword(null);
                    });

                stdSisCourse
                    .getSisSchedules()
                    .forEach(sisSchedule -> {
                        sisSchedule.setSisSignIns(null);
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
                sisSchedule.setSisSignIns(null);
            });

        sisCourse.setSisJoinCourseList(null);
        sisCourse.setMonitor(null);

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
        sisCourseRepository.save(sisCourse);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
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
                    sisMonitorTrans.getSmtStatus().equals(SisMonitorTrans.SmtStatus.AGREE) &&
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

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    public JSONObject getTransCourses(@NotNull SisUser sisUser,
                                      SisMonitorTrans.SmtStatus smtStatus) {
        SisMonitorTrans tSisMonitorTrans = new SisMonitorTrans();
        tSisMonitorTrans.setSisUser(sisUser);
        tSisMonitorTrans.setSmtStatus(smtStatus);

        List<SisMonitorTrans> sisMonitorTransList = sisMonitorTransRepository
            .findAll(Example.of(tSisMonitorTrans))
            .stream()
            .peek(sisMonitorTrans -> {
                SisSchedule sisSchedule =
                    sisMonitorTrans.getSmtId().getSisSchedule();
                sisSchedule.setSisSupervisions(null);
                sisSchedule.setSisSignIns(null);
                sisSchedule.setSisSupervisions(null);

                SisCourse sisCourse = sisSchedule.getSisCourse();

                sisCourse
                    .getSisJoinCourseList()
                    .forEach(sisJoinCourse -> {
                        sisJoinCourse.setSisCourse(null);

                        SisUser joinUser = sisJoinCourse.getSisUser();
                        ;
                        joinUser.setSisCourses(null);
                        joinUser.setSuPassword(null);
                        joinUser.setSisMonitorTrans(null);
                        joinUser.setSisJoinCourses(null);
                        joinUser.setSisSignInDetails(null);
                    });
                sisCourse.setSisSchedules(null);

                Optional.ofNullable(sisCourse.getMonitor())
                    .ifPresent(monitor -> {
                        monitor.setSisCourses(null);
                        monitor.setSuPassword(null);
                        monitor.setSisJoinCourses(null);
                        monitor.setSisSignInDetails(null);
                        monitor.setSisMonitorTrans(null);
                    });
            })
            .collect(Collectors.toList());

        entityManager.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("array", sisMonitorTransList);
        jsonObject.put("arrSize", sisMonitorTransList.size());
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

        sisMonitorTrans.setSmtStatus(SisMonitorTrans.SmtStatus.UNTREATED);
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

        stdSisMonitorTrans.setSmtStatus(sisMonitorTrans.getSmtStatus());
        sisMonitorTransRepository.saveAndFlush(sisMonitorTrans);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        return jsonObject;
    }
}

