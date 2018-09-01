package team.a9043.sign_in_system.entity;

import io.swagger.annotations.ApiModelProperty;
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
public class SisLocation {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("流水号")
    private Integer slId;

    @Column
    private String slName;

    @Column
    private Double slLong;

    @Column
    private Double slLat;
}
