package team.a9043.sign_in_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.a9043.sign_in_system.entity.SisSchedule;

/**
 * @author a9043
 */
public interface SisScheduleRepository extends
    JpaRepository<SisSchedule, Integer> {
}
