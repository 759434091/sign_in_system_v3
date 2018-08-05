package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 角色权限实体
 * <p>
 * abbr: sr
 *
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
