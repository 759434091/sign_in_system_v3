package team.a9043.sign_in_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.a9043.sign_in_system.entity.SisUser;

/**
 * @author a9043
 */
public interface SisUserRepository extends JpaRepository<SisUser, String> {
}
