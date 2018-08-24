package team.a9043.sign_in_system.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ssId", " ssiWeek"})
})
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@ApiModel("排课签到记录")
public class SisSignIn {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ssiId;

    @ManyToOne
    @JoinColumn(name = "ssId", referencedColumnName = "ssId")
    @ApiModelProperty("排课")
    private SisSchedule sisSchedule;

    @ApiModelProperty("签到周")
    @Column
    private Integer ssiWeek;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "ssiId", referencedColumnName = "ssiId")
    private Collection<SisSignInDetail> sisSignInDetails = new ArrayList<>();
}
