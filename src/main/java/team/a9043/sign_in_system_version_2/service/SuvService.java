package team.a9043.sign_in_system_version_2.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system_version_2.exception.InsufficientPermissionsException;
import team.a9043.sign_in_system_version_2.exception.InvalidParameterException;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.mapper.AdmMapper;
import team.a9043.sign_in_system_version_2.mapper.SupervisionMapper;
import team.a9043.sign_in_system_version_2.pojo.*;
import team.a9043.sign_in_system_version_2.pojo.extend.ScheduleWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.SignInRecOnLeaveWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.SuvRecWithSuv;
import team.a9043.sign_in_system_version_2.util.PowerOperation;
import team.a9043.sign_in_system_version_2.util.TransSchedule;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service("suvService")
public class SuvService {
    @Resource
    private SupervisionMapper supervisionMapper;
    @Resource
    private AdmMapper admMapper;
    private final TransSchedule transSchedule;

    @Autowired
    public SuvService(TransSchedule transSchedule) {
        this.transSchedule = transSchedule;
    }

    public JSONArray getSuvPool(LocalDateTime currentDateTime) {
        List<Map<String, Object>> suvRawList = supervisionMapper.getSuvRaw(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                null,
                null);

        if (suvRawList.size() <= 0) {
            return new JSONArray();
        }

        Set<Integer> schIdSet = suvRawList
                .stream()
                .map(map -> Integer.parseInt(String.valueOf((long) map.get("schId"))))
                .collect(Collectors.toSet());

        List<ScheduleWithCozDtl> scheduleWithCozDtlList = admMapper.getScheduleWithCozDtl(schIdSet);

        JSONArray suvList = new JSONArray(suvRawList);

        suvList.forEach(suvObj -> {
            JSONObject suv = (JSONObject) suvObj;
            scheduleWithCozDtlList
                    .stream()
                    .filter(tSchedule -> tSchedule.getSchId().equals((int) suv.getLong("schId")))
                    .limit(1)
                    .findFirst()
                    .ifPresent(scheduleWithCozDtl -> suv.put("scheduleWithCozDtl", new JSONObject(scheduleWithCozDtl)));
        });

        return suvList;
    }

    public boolean receiveSuvSchedule(LocalDateTime currentDateTime, User user, Integer schId, List<Integer> suvWeekList) throws ParameterNotFoundException {
        return supervisionMapper.receiveSuvSchedule(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                user.getUsrId(),
                Optional.ofNullable(schId).orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect schId, suvWeekList.")),
                Optional.ofNullable(suvWeekList).orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect schId, suvWeekList."))) > 0;
    }

    public List<Supervision> getSuvCourseList(User user, LocalDateTime currentDateTime, Integer curWeek) {
        return supervisionMapper
                .getSuvCourseList(
                        transSchedule.getYear(currentDateTime),
                        transSchedule.getTerm(currentDateTime),
                        curWeek,
                        false,
                        Optional
                                .ofNullable(user)
                                .map(User::getUsrId)
                                .orElse(null));
    }

    public List<SignInRecOnLeaveWithCozDtl> getStuLeaveRec(User user, LocalDateTime currentDateTime) {
        return supervisionMapper.getStuLeaveRec(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                user.getUsrId());
    }

    public SuvRecWithSuv getSuvRec(LocalDateTime currentDateTime, Supervision supervision) throws ParameterNotFoundException {
        return supervisionMapper.getSuvRec(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                Optional.ofNullable(supervision.getSuvId()).orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect suvId."))
        );
    }

    @Transactional
    public boolean insertSuvRec(User user, SuvRec suvRec, LocalDateTime currentDateTime) throws ParameterNotFoundException, InsufficientPermissionsException {
        try {
            boolean permissionCheck = supervisionMapper.getUserBySuvId(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    suvRec.getSupervisionSuvId()).getUsrId().equals(user.getUsrId());
            if (!permissionCheck) {
                throw new InsufficientPermissionsException(this.getClass().getName() + ".insertSuvRec");
            }

            return supervisionMapper
                    .insertSuvRec(
                            transSchedule.getYear(currentDateTime),
                            transSchedule.getTerm(currentDateTime),
                            suvRec.getSuvRecNum(),
                            suvRec.getSuvRecBadNum(),
                            suvRec.getSuvRecName(),
                            suvRec.getSuvRecInfo(),
                            suvRec.getSupervisionSuvId()
                    ) > 0;
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect suvRecNum, suvRecBadNum, suvRecName, suvRecInfo, supervisionSuvId.");
        }
    }

    @Transactional
    public boolean approveOrRejectLeave(User user, SignInRec signInRec, LocalDateTime currentDateTime) throws InsufficientPermissionsException, ParameterNotFoundException, InvalidParameterException {
        try {
            boolean power = supervisionMapper.getUserBySiId(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    signInRec.getSignInSiId()).getUsrId().equals(user.getUsrId());
            if (!power) {
                throw new InsufficientPermissionsException(this.getClass().getName() + ".approveOrRejectLeave");
            }

            //开始审批
            switch (signInRec.getSirApprove()) {
                case 1:
                    return supervisionMapper.updateSignInRec(
                            transSchedule.getYear(currentDateTime),
                            transSchedule.getTerm(currentDateTime),
                            1,
                            signInRec.getSirId()) > 0;
                case 2:
                    return supervisionMapper.deleteSignInRec(
                            transSchedule.getYear(currentDateTime),
                            transSchedule.getTerm(currentDateTime),
                            signInRec.getSirId()
                    ) > 0;
                default:
                    throw new InvalidParameterException("signInRec.getSirApprove() 1 = approve, 2 = reject");
            }
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect signInSiId, sirId, sirApprove.");
        }
    }

    @Transactional
    public SignIn getSignInBySchAndWeek(Supervision supervision, LocalDateTime currentDateTime) throws ParameterNotFoundException {
        try {
            return supervisionMapper.getSignInBySchAndWeek(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    supervision.getSuvWeek(),
                    supervision.getScheduleWithCozDtl().getSchId());
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect suvWeek, scheduleSchId.");
        }
    }

    @Transactional
    public boolean changePower(User user, PowerOperation operation, Supervision supervision, LocalDateTime currentDateTime) throws ParameterNotFoundException, InsufficientPermissionsException {
        try {
            if (!supervision.getStudent().getUsrId().equals(user.getUsrId())) {
                throw new InsufficientPermissionsException(this.getClass().getName() + ".changePower");
            }

            if (operation.equals(PowerOperation.GIVE_UP)) {
                return supervisionMapper.changePower(
                        transSchedule.getYear(currentDateTime),
                        transSchedule.getTerm(currentDateTime),
                        null,
                        supervision.getSuvId()) > 0;
            } else {
                return supervisionMapper.changePower(
                        transSchedule.getYear(currentDateTime),
                        transSchedule.getTerm(currentDateTime),
                        supervision.getStudent().getUsrId(),
                        supervision.getSuvId()) > 0;
            }
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect suvId, student.usrId.");
        }
    }

    @Transactional
    public boolean changeSignIn(User user, SignIn signIn, LocalDateTime currentDateTime) throws InsufficientPermissionsException, ParameterNotFoundException {
        try {
            boolean power = supervisionMapper.getUserBySchAndWeek(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    signIn.getSiWeek(),
                    signIn.getScheduleSchId()).getUsrId().equals(user.getUsrId());
            if (!power) {
                throw new InsufficientPermissionsException(this.getClass().getName() + ".changeSignIn");
            }

            SignIn dataBaseSignIn = supervisionMapper.getSignInBySchAndWeek(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    signIn.getSiWeek(),
                    signIn.getScheduleSchId());
            //数据库无记录
            if (dataBaseSignIn == null) {
                if (!signIn.getSiAuto() && signIn.getSiTime().isEqual(LocalDateTime.of(1970, 1, 1, 0, 0, 0))) {
                    return true;
                }
                return supervisionMapper.insertSignIn(
                        transSchedule.getYear(currentDateTime),
                        transSchedule.getTerm(currentDateTime),
                        signIn.getSiWeek(),
                        signIn.getSiTime() == null ? LocalDateTime.of(1970, 1, 1, 0, 0, 0) : signIn.getSiTime(),
                        signIn.getSiAuto() == null ? true : signIn.getSiAuto(),
                        signIn.getScheduleSchId()) > 0;
            }

            //请求删除
            if (!signIn.getSiAuto() && signIn.getSiTime().isEqual(LocalDateTime.of(1970, 1, 1, 0, 0, 0))) {
                return supervisionMapper.deleteSignIn(
                        transSchedule.getYear(currentDateTime),
                        transSchedule.getTerm(currentDateTime),
                        signIn.getSiWeek(),
                        signIn.getScheduleSchId()) >= 0;
            }

            //请求更新
            return supervisionMapper.updateSignIn(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    signIn.getSiWeek(),
                    signIn.getSiTime() == null ? LocalDateTime.of(1970, 1, 1, 0, 0, 0) : signIn.getSiTime(),
                    signIn.getSiAuto() == null ? true : signIn.getSiAuto(),
                    signIn.getScheduleSchId()) > 0;
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect object signIn");
        }
    }

    public List<SuvTrans> getSuvTrans(User user, LocalDateTime currentDateTime) {
        return supervisionMapper.getSuvTransByUser(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                user.getUsrId());
    }

    @Transactional
    public boolean insertSuvTrans(User user, SuvTrans suvTrans, LocalDateTime currentDateTime) throws ParameterNotFoundException, InsufficientPermissionsException {
        try {
            if (!user.getUsrId().equals(suvTrans.getSupervision().getStudent().getUsrId())) {
                throw new InsufficientPermissionsException(this.getClass().getName() + ".insertSuvTrans");
            }

            return supervisionMapper.insertSuvTrans(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    suvTrans.getUserUsrId(),
                    suvTrans.getSupervision().getSuvId()) > 0;
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect supervision.suvId, supervision.student.usrId, userUsrId.");
        }
    }

    @Transactional
    public boolean rejectSuvTrans(User user, SuvTrans suvTrans, LocalDateTime currentDateTime) throws InsufficientPermissionsException, ParameterNotFoundException {
        try {
            if (!user.getUsrId().equals(suvTrans.getUserUsrId())) {
                throw new InsufficientPermissionsException(this.getClass().getName() + ".insertSuvTrans");
            }
            return supervisionMapper.deleteSuvTrans(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    suvTrans.getUserUsrId(),
                    suvTrans.getSupervision().getSuvId()) > 0;
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect supervision.suvId, supervision.student.usrId, userUsrId.");
        }
    }

    @Transactional
    public boolean acceptSuvTrans(User user, SuvTrans suvTrans, LocalDateTime currentDateTime) throws InsufficientPermissionsException, ParameterNotFoundException {
        try {
            if (!user.getUsrId().equals(suvTrans.getUserUsrId())) {
                throw new InsufficientPermissionsException(this.getClass().getName() + ".insertSuvTrans");
            }

            boolean changeRes = supervisionMapper.changePower(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    suvTrans.getUserUsrId(),
                    suvTrans.getSupervision().getSuvId()) > 0;
            if (!changeRes) {
                return false;
            }

            //success
            return supervisionMapper.deleteSuvTrans(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    suvTrans.getUserUsrId(),
                    suvTrans.getSupervision().getSuvId()) > 0;
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect supervision.suvId, supervision.student.usrId, userUsrId.");
        }
    }
}
