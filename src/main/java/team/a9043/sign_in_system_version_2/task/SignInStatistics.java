package team.a9043.sign_in_system_version_2.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system_version_2.mapper.TaskMapper;
import team.a9043.sign_in_system_version_2.util.TransSchedule;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class SignInStatistics {
    @Resource
    private TaskMapper taskMapper;
    private final TransSchedule transSchedule;

    @Autowired
    public SignInStatistics(TransSchedule transSchedule) {
        this.transSchedule = transSchedule;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void updateCozAttRate() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Map> cozAttRateList = taskMapper.getCozAttRate(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime));
        taskMapper.updateCozAttRate(cozAttRateList);
    }
}
