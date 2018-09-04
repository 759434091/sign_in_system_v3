package team.a9043.sign_in_system.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.mapper.SisCourseMapper;
import team.a9043.sign_in_system.mapper.SisScheduleMapper;
import team.a9043.sign_in_system.mapper.SisSupervisionMapper;
import team.a9043.sign_in_system.pojo.*;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class AttRateSchedule {
    @Resource
    private SisCourseMapper sisCourseMapper;
    @Resource
    private SisSupervisionMapper sisSupervisionMapper;
    @Resource
    private SisScheduleMapper sisScheduleMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void attRateSchedule() {
        //
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void supervisionSchedule() {
        try {
            int week = JudgeTimeUtil.getWeek(LocalDate.now()) - 1;

            //获得督导课程
            SisCourseExample sisCourseExample = new SisCourseExample();
            sisCourseExample.createCriteria().andScNeedMonitorEqualTo(true).andSuIdIsNotNull();
            List<SisCourse> sisCourseList = sisCourseMapper.selectByExample(sisCourseExample);

            //获得课程排课
            List<String> scIdList = sisCourseList.parallelStream()
                .map(SisCourse::getScId)
                .distinct()
                .collect(Collectors.toList());
            if (scIdList.isEmpty())
                return;
            SisScheduleExample sisScheduleExample = new SisScheduleExample();
            sisScheduleExample.createCriteria().andScIdIn(scIdList);
            List<SisSchedule> sisScheduleList = sisScheduleMapper.selectByExample(sisScheduleExample);

            //获得督导历史
            List<Integer> ssIdList = sisScheduleList.parallelStream().map(SisSchedule::getSsId).distinct().collect(Collectors.toList());
            if (ssIdList.isEmpty())
                return;

            SisSupervisionExample sisSupervisionExample = new SisSupervisionExample();
            sisSupervisionExample.createCriteria().andSsIdIn(ssIdList).andSsvWeekLessThanOrEqualTo(week);
            List<SisSupervision> sisSupervisionList = sisSupervisionMapper.selectByExample(sisSupervisionExample);

            sisCourseList.parallelStream()
                .map(sisCourse -> {
                    String scId = sisCourse.getScId();
                    String suId = sisCourse.getSuId();

                    List<SisSchedule> scheduleList = sisScheduleList.parallelStream()
                        .filter(sisSchedule -> sisSchedule.getScId().equals(scId))
                        .collect(Collectors.toList());

                    if (scheduleList.isEmpty())
                        return null;
                    int totalLackNum = scheduleList.parallelStream()
                        .mapToInt(sisSchedule -> {
                            Integer ssId = sisSchedule.getSsId();
                            int countNum = (int) sisSupervisionList.parallelStream()
                                .filter(sisSupervision -> sisSupervision.getSsId().equals(ssId))
                                .count();

                            int lackNum = week - countNum;
                            lackNum = lackNum < 0 ? 0 : lackNum;
                            return lackNum;
                        })
                        .sum();
                    SisUserInfo sisUserInfo = new SisUserInfo();
                    sisUserInfo.setSuId(suId);
                    sisUserInfo.setLackNum(totalLackNum);
                    return sisUserInfo;
                })
                .collect(ArrayList::new, (arrayList, sisUserInfo) -> {
                    if (!arrayList.contains(sisUserInfo))
                        arrayList.add(sisUserInfo);
                }, (arr1, arr2) -> {

                });
        } catch (InvalidTimeParameterException e) {
            e.printStackTrace();
        }
    }
}
