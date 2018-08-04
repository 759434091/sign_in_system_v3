package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author a9043
 */
@Entity
@Getter
@Setter
public class SisRole {
    @Id
    private Integer srId;
    @Column(length = 20)
    private String roleName;
}
