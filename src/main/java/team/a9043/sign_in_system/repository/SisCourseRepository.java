package team.a9043.sign_in_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team.a9043.sign_in_system.entity.SisCourse;

/**
 * @author a9043
 */
public interface SisCourseRepository extends JpaRepository<SisCourse, String> {
    Page<SisCourse> findAllByScNeedMonitorIsAndMonitorNotNull(Boolean scNeedMonitor,
                                                                    Pageable pageable);

    Page<SisCourse> findAllByScNeedMonitorIsAndMonitorIsNull(Boolean scNeedMonitor,
                                                                   Pageable pageable);

    Page<SisCourse> findAllByScNeedMonitorIs(Boolean scNeedMonitor,
                                                   Pageable pageable);
}
