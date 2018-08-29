package team.a9043.sign_in_system.other;

import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.mapper.SisJoinCourseMapper;
import team.a9043.sign_in_system.mapper.SisScheduleMapper;
import team.a9043.sign_in_system.pojo.SisJoinCourse;
import team.a9043.sign_in_system.pojo.SisJoinCourseExample;
import team.a9043.sign_in_system.pojo.SisSchedule;
import team.a9043.sign_in_system.pojo.SisScheduleExample;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
            }).toCompletableFuture();
        CompletableFuture<List<SisSchedule>> listCompletableFuture1 =
            CompletableFuture.supplyAsync(() -> {
                SisScheduleExample sisScheduleExample =
                    new SisScheduleExample();
                sisScheduleExample.createCriteria().andScIdIn(scIdList);
                return sisScheduleMapper.selectByExample(sisScheduleExample);
            }).toCompletableFuture();

        CompletableFuture.allOf(listCompletableFuture,
            listCompletableFuture1).whenComplete((a, b) -> {
            if (null != b)
                b.printStackTrace();
        }).join();

        log.info("end");

    }
}
