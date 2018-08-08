package team.a9043.sign_in_system.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 课程表实体
 * <p>
 * abbr: sc
 *
 * @author a9043
 */
@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@ApiModel("课程实体")
public class SisCourse {
    @Id
    @Column(length = 15)
    @NotNull
    @ApiModelProperty("课程序号")
    private String scId;

    @Column(length = 50, nullable = false)
    @ApiModelProperty("课程名")
    private String scName;

    @Column
    @ApiModelProperty("课程序号")
    private Integer scMaxSize;

    @Column
    @ApiModelProperty("实际人数")
    private Integer scActSize;

    @Column
    @ApiModelProperty("上课年级")
    private Integer scGrade;

    @Column
    @ApiModelProperty("到勤率")
    private BigDecimal scAttRate;

    @Column(nullable = false)
    @NotNull
    @ApiModelProperty("是否需要签到")
    private Boolean scNeedMonitor;

    /**
     * the monitor
     */
    @ManyToOne
    @JoinColumn(name = "suId", referencedColumnName = "suId")
    @ApiModelProperty("督导员")
    private SisUser monitor;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "scId", referencedColumnName = "scId")
    @ApiModelProperty("排课")
    private Collection<SisSchedule> sisSchedules = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "scId", referencedColumnName = "scId")
    @ApiModelProperty("课程参与人员")
    private Collection<SisJoinCourse> sisJoinCourseList = new ArrayList<>();
}
