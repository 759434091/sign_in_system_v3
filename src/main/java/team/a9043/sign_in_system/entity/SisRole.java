package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author a9043
 */
@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
public class SisRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer srId;
    @Column(length = 20)
    private String roleName;
}
