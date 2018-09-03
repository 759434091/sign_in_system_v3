package team.a9043.sign_in_system.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.mapper.SisCourseMapper;

import javax.annotation.Resource;

@Service
public class AttRateSchedule {
    @Resource
    private SisCourseMapper sisCourseMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void attRateSchedule() {
        //
    }
}
