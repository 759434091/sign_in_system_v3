package team.a9043.sign_in_system.other;

import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.mapper.SisJoinCourseMapper;
import team.a9043.sign_in_system.mapper.SisScheduleMapper;
import team.a9043.sign_in_system.pojo.SisJoinCourse;
import team.a9043.sign_in_system.pojo.SisJoinCourseExample;
import team.a9043.sign_in_system.pojo.SisSchedule;
import team.a9043.sign_in_system.pojo.SisScheduleExample;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

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
}