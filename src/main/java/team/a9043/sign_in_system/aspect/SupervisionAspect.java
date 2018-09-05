package team.a9043.sign_in_system.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import team.a9043.sign_in_system.mapper.SisCourseMapper;
import team.a9043.sign_in_system.mapper.SisScheduleMapper;
import team.a9043.sign_in_system.mapper.SisSignInMapper;
import team.a9043.sign_in_system.mapper.SisSupervisionMapper;
import team.a9043.sign_in_system.pojo.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Component
@Aspect
@Slf4j
public class SupervisionAspect {
    @Resource
    private SisScheduleMapper sisScheduleMapper;
    @Resource
    private SisSupervisionMapper sisSupervisionMapper;
    @Resource
    private SisCourseMapper sisCourseMapper;
    @Resource
    private SisSignInMapper sisSignInMapper;

    @AfterReturning(value = "execution(* team.a9043.sign_in_system." +
        "service.MonitorService.insertSupervision(..)) && args(sisUser,ssId," +
        "sisSupervision,localDateTime)",
        returning = "res", argNames = "sisUser,ssId,sisSupervision," +
        "localDateTime,res")
    public void afterSupervision(SisUser sisUser,
                                 Integer ssId,
                                 SisSupervision sisSupervision,
                                 LocalDateTime localDateTime,
                                 JSONObject res) {
        if (!res.getBoolean("success"))
            return;

        updateAttRate(ssId);
    }

    public void updateAttRate(Integer ssId) {
        SisSchedule sisSchedule = sisScheduleMapper.selectByPrimaryKey(ssId);
        if (null == ssId)
            return;

        String scId = sisSchedule.getScId();

        SisCourse sisCourse = sisCourseMapper.selectByPrimaryKey(scId);
        if (null == sisCourse)
            return;
        Integer actNum = sisCourse.getScActSize();
        if (null == actNum)
            return;

        SisScheduleExample sisScheduleExample = new SisScheduleExample();
        sisScheduleExample.createCriteria().andScIdEqualTo(scId);
        List<Integer> ssIdList =
            sisScheduleMapper.selectByExample(sisScheduleExample).stream().map(SisSchedule::getSsId).collect(Collectors.toList());
        if (ssIdList.isEmpty())
            return;

        SisSupervisionExample sisSupervisionExample =
            new SisSupervisionExample();
        sisSupervisionExample.createCriteria().andSsIdIn(ssIdList);
        List<SisSupervision> sisSupervisionList =
            sisSupervisionMapper.selectByExample(sisSupervisionExample);

        SisSignInExample sisSignInExample = new SisSignInExample();
        sisScheduleExample.createCriteria().andSsIdIn(ssIdList);
        List<SisSignIn> sisSignInList =
            sisSignInMapper.selectByExample(sisSignInExample);

        DoubleStream doubleStream = sisSupervisionList.parallelStream()
            .mapToDouble(tSisSupervision -> {
                double halfMan = 0;
                if (null != tSisSupervision.getSsvMobileNum()) {
                    halfMan += tSisSupervision.getSsvMobileNum();
                }
                if (null != tSisSupervision.getSsvSleepNum()) {
                    halfMan += tSisSupervision.getSsvSleepNum();
                }

                halfMan /= 2;
                double suvActNum =
                    (tSisSupervision.getSsvActualNum() - halfMan);
                suvActNum = suvActNum < 0 ? 0 : suvActNum;
                return suvActNum / actNum;
            });

        DoubleStream doubleStream1 = sisSignInList.parallelStream()
            .mapToDouble(SisSignIn::getSsiAttRate);

        double totalRate1 = doubleStream.average().orElse(-1);
        double totalRate2 = doubleStream1.average().orElse(-1);
        Double totalRate;

        if (totalRate1 < 0 && totalRate2 >= 0)
            totalRate = totalRate2;
        else if (totalRate2 < 0 && totalRate1 >= 0)
            totalRate = totalRate1;
        else if (totalRate1 >= 0 & totalRate2 >= 0)
            totalRate = (totalRate1 + totalRate2) / 2;
        else
            totalRate = null;

        sisCourse.setScAttRate(null == totalRate ?
            null : BigDecimal.valueOf(totalRate));
        boolean resB = sisCourseMapper.updateByPrimaryKey(sisCourse) > 0;
        if (!resB)
            log.error("con not update att rate at: " + scId + ", " + totalRate);
    }
}
