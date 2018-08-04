package team.a9043.sign_in_system.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author a9043
 */
@Entity
public class SisMonitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer smId;
}
