package team.a9043.sign_in_system.other;

import lombok.extern.java.Log;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.mapper.SisJoinCourseMapper;
import team.a9043.sign_in_system.mapper.SisScheduleMapper;
import team.a9043.sign_in_system.mapper.SisUserInfoMapper;
import team.a9043.sign_in_system.pojo.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class OtherTest {
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;
    @Resource
    private SisScheduleMapper sisScheduleMapper;
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Resource
    private SisUserInfoMapper sisUserInfoMapper;

    @Test
    public void test() {
        List<String> scIdList = new ArrayList<>();
        scIdList.add("A");

        CompletableFuture<List<SisJoinCourse>> listCompletableFuture =
            CompletableFuture.supplyAsync(() -> {
                SisJoinCourseExample sisJoinCourseExample =
                    new SisJoinCourseExample();
                sisJoinCourseExample.createCriteria().andScIdIn(scIdList);
                return sisJoinCourseMapper.selectByExample(sisJoinCourseExample);
            }, taskExecutor).toCompletableFuture();
        CompletableFuture<List<SisSchedule>> listCompletableFuture1 =
            CompletableFuture.supplyAsync(() -> {
                SisScheduleExample sisScheduleExample =
                    new SisScheduleExample();
                sisScheduleExample.createCriteria().andScIdIn(scIdList);
                return sisScheduleMapper.selectByExample(sisScheduleExample);
            }, taskExecutor).toCompletableFuture();

        CompletableFuture.allOf(listCompletableFuture,
            listCompletableFuture1).join();

        log.info("end");
    }

    @Test
    public void test2() {
        AtomicBoolean isEnd = new AtomicBoolean();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        threadPoolTaskScheduler.schedule(() -> isEnd.set(true),
            calendar.toInstant());

        while (!isEnd.get()) {

        }
        log.info("end: " + isEnd);
    }

    @Test
    public void test3() {
        String ssSuspension = "1,a,2,c.,-1";
        List<Integer> integers = Arrays.stream(ssSuspension.split(","))
            .map(String::trim)
            .map(s -> {
                try {
                    return Integer.valueOf(s);
                } catch (NumberFormatException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .filter(integer -> integer > 0)
            .collect(Collectors.toList());
        log.info(new JSONArray(integers).toString(2));
    }

    @Test
    public void teat4() {
        SisUserInfo sisUserInfo3 = new SisUserInfo();
        sisUserInfo3.setSuId("2016220401001");
        sisUserInfo3.setSuiLackNum(1);

        SisUserInfo sisUserInfo1 = new SisUserInfo();
        sisUserInfo1.setSuId("2016220401001");
        sisUserInfo1.setSuiLackNum(5);


        SisUserInfo sisUserInfo4 = new SisUserInfo();
        sisUserInfo4.setSuId("2016220401001");
        sisUserInfo4.setSuiLackNum(5);


        SisUserInfo sisUserInfo2 = new SisUserInfo();
        sisUserInfo2.setSuId("2016220401007");
        sisUserInfo2.setSuiLackNum(4);

        Stream<SisUserInfo> sisUserInfoStream = Stream.of(sisUserInfo3, sisUserInfo1, sisUserInfo2, sisUserInfo4);
        List<SisUserInfo> sisUserInfoList = sisUserInfoStream
            .collect(ArrayList::new,
                (list, sisUserInfo) -> {
                    int idx = list.indexOf(sisUserInfo);
                    if (-1 == idx) {
                        list.add(sisUserInfo);
                        return;
                    }

                    SisUserInfo stdSisUserInfo = list.get(idx);
                    stdSisUserInfo.setSuiLackNum(stdSisUserInfo.getSuiLackNum() + sisUserInfo.getSuiLackNum());
                },
                (arr1, arr2) -> arr2.forEach(sisUserInfo -> {
                    int idx = arr1.indexOf(sisUserInfo);
                    if (-1 == idx) {
                        arr1.add(sisUserInfo);
                        return;
                    }

                    SisUserInfo stdSisUserInfo = arr1.get(idx);
                    stdSisUserInfo.setSuiLackNum(stdSisUserInfo.getSuiLackNum() + sisUserInfo.getSuiLackNum());
                }));
        sisUserInfoMapper.insertList(sisUserInfoList);
    }
}
