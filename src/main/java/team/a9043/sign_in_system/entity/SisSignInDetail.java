package team.a9043.sign_in_system.entity;

import io.swagger.annotations.ApiModel;
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
@ApiModel("排课签到记录详细")
public class SisSignInDetail {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("流水号")
    private Integer ssidId;

    @Column
    @ApiModelProperty("签到状态")
    private Boolean ssidStatus;

    @ManyToOne
    @JoinColumn(name = "ssiId", referencedColumnName = "ssiId")
    @ApiModelProperty("签到学生")
    private SisSignIn sisSignIn;

    @ManyToOne
    @JoinColumn(name = "suId", referencedColumnName = "suId")
    @ApiModelProperty("签到学生")
    private SisUser sisUser;
}
