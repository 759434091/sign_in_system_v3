package team.a9043.task;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.JavaUtil.TransSchedule;
import team.a9043.pojo.Location;
import team.a9043.pojo.Schedule;
import team.a9043.pojo.SuvMan;
import team.a9043.service.AdminService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduleTask {
    private static final Logger infoLog = LogManager.getLogger(ScheduleTask.class);
    private final AdminService adminService;
    private final RedisTemplate<String, Object> redisTemplate;


    @Autowired
    public ScheduleTask(AdminService adminService, RedisTemplate<String, Object> redisTemplate) {
        this.adminService = adminService;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void updateCoz() {
        adminService.updateCozAttRate();
        adminService.updateCozNumber();
    }

    @Scheduled(cron = "0 20 8 * * *")
    public void updateSignIn_1() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(1);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 10 9 * * *")
    public void updateSignIn_2() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(2);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 10 10 * * *")
    public void updateSignIn_3() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(3);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 00 11 * * *")
    public void updateSignIn_4() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(4);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 20 14 * * *")
    public void updateSignIn_5() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(5);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 10 15 * * *")
    public void updateSignIn_6() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(6);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 10 16 * * *")
    public void updateSignIn_7() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(7);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 0 17 * * *")
    public void updateSignIn_8() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(8);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 20 19 * * *")
    public void updateSignIn_9() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(9);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 10 20 * * *")
    public void updateSignIn_10() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(10);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 0 21 * * *")
    public void updateSignIn_11() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(11);
        setReadySignIn(scheduleList);
    }

    @Scheduled(cron = "0 50 21 * * *")
    public void updateSignIn_12() {
        Set<String> keys = redisTemplate.keys("suvMan_" + "*");
        redisTemplate.delete(keys);
        keys = redisTemplate.keys("schedule_" + "*");
        redisTemplate.delete(keys);
        List<Schedule> scheduleList = adminService.findScheduleByTime(12);
        setReadySignIn(scheduleList);
    }

    private void setReadySignIn(List<Schedule> scheduleList) {
        for (Schedule schedule : scheduleList) {
/*            SuvMan suvMan = adminService.findSuvMan(schedule.getSchId(), TransSchedule.getWeek(LocalDateTime.now()));
            if (suvMan != null) {
                redisTemplate.opsForValue().set("suvMan_" + schedule.getSchId() + "_" + suvMan.getSiWeek(), suvMan, 60 * 40, TimeUnit.SECONDS);
                infoLog.debug("suvMan_" + schedule.getSchId() + "_" + suvMan.getSiWeek());
                infoLog.trace(JSON.toJSONString(suvMan));
            }*/
            redisTemplate.opsForValue().set("schedule_" + schedule.getSchId(), schedule, 60 * 40, TimeUnit.SECONDS);
            infoLog.debug("schedule_" + schedule.getSchId());
            infoLog.trace(JSON.toJSONString(schedule));
        }
    }

    /*@Scheduled(cron = "0 * * * * *")*/
    public void updateSignIn_x() {
        Location location = new Location();
        location.setLocLat(30.6746773009);
        location.setLocLon(104.1028136015);
        location.setLocId(1);
        Schedule schedule = new Schedule();
        schedule.setSchId(4086);
        schedule.setSchWeek(16);
        schedule.setSchDay(4);
        schedule.setSchFortnight(0);
        schedule.setSchTerm(true);
        schedule.setSchYear(2017);
        schedule.setSchTime(1);
        schedule.setLocation(location);
        redisTemplate.opsForValue().set("schedule_4086", schedule, 50, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set("suvMan_7_11", adminService.findSuvMan(742, 11), 50, TimeUnit.MINUTES);
        infoLog.trace(JSON.toJSONString(redisTemplate.opsForValue().get("suvMan_1_5")));
    }
}
