package team.a9043.sign_in_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.a9043.sign_in_system.entity.SisSchedule;

/**
 * @author a9043
 */
public interface SisScheduleRepository extends
    JpaRepository<SisSchedule, Integer> {

    @Modifying
    @Query("update SisSchedule set ssNeedMonitor = :ssNeedMonitor " +
        "where ssId = :ssId")
    int modifySsNeedMonitor(@Param("ssId") Integer ssId,
                               @Param("ssNeedMonitor") Boolean ssNeedMonitor);
}
