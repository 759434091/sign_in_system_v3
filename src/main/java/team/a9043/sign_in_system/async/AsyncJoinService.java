package team.a9043.sign_in_system.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import team.a9043.sign_in_system.mapper.*;
import team.a9043.sign_in_system.pojo.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author a9043
 */
@Component
public class AsyncJoinService {
    @Resource
    private SisUserMapper sisUserMapper;
    @Resource
    private SisScheduleMapper sisScheduleMapper;
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;
    @Resource
    private SisSupervisionMapper sisSupervisionMapper;
    @Resource
    private SisCourseMapper sisCourseMapper;

    @Async
    public Future<List<SisUser>> joinSisUserById(List<String> suIdList) {
        if (suIdList.isEmpty()) {
            return new AsyncResult<>(new ArrayList<>());
        } else {
            SisUserExample sisUserExample = new SisUserExample();
            sisUserExample.createCriteria().andSuIdIn(suIdList);
            return new AsyncResult<>(sisUserMapper.selectByExample(sisUserExample));
        }
    }

    public Future<List<SisCourse>> joinSisCourseById(List<String> scIdList) {
        if (scIdList.isEmpty()) {
            return new AsyncResult<>(new ArrayList<>());
        } else {
            SisCourseExample sisCourseExample = new SisCourseExample();
            sisCourseExample.createCriteria().andScIdIn(scIdList);
            return new AsyncResult<>(sisCourseMapper.selectByExample(sisCourseExample));
        }
    }

    @Async
    public Future<List<SisSchedule>> joinSisScheduleByForeignKey(List<String> scIdList) {
        if (scIdList.isEmpty()) {
            return new AsyncResult<>(new ArrayList<>());
        } else {
            SisScheduleExample sisScheduleExample = new SisScheduleExample();
            sisScheduleExample.createCriteria().andScIdIn(scIdList);
            return new AsyncResult<>(sisScheduleMapper.selectByExample(sisScheduleExample));
        }
    }

    @Async
    public Future<List<SisJoinCourse>> joinSisJoinCourseByForeignKey(List<String> scIdList) {
        if (scIdList.isEmpty()) {
            return new AsyncResult<>(new ArrayList<>());
        } else {
            SisJoinCourseExample sisJoinCourseExample =
                new SisJoinCourseExample();
            sisJoinCourseExample.createCriteria().andScIdIn(scIdList);
            return new AsyncResult<>(sisJoinCourseMapper.selectByExample(sisJoinCourseExample));
        }
    }

    @Async
    public Future<List<SisSupervision>> joinSisSupervisionByForeignKey(List<Integer> ssIdList) {
        if (ssIdList.isEmpty()) {
            return new AsyncResult<>(new ArrayList<>());
        } else {
            SisSupervisionExample sisSupervisionExample =
                new SisSupervisionExample();
            sisSupervisionExample.createCriteria().andSsIdIn(ssIdList);
            return new AsyncResult<>(sisSupervisionMapper.selectByExample(sisSupervisionExample));
        }
    }
}
