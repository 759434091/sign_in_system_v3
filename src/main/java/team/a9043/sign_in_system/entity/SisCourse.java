package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
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
public class SisCourse {
    @Id
    @Column(length = 15)
    private String scId;

    @Column(length = 50, nullable = false)
    private String scName;

    @Column
    private Integer scMaxSize;

    @Column
    private Integer scActSize;

    @Column
    private Integer scGrade;

    @Column
    private BigDecimal scAttRate;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "scId", referencedColumnName = "scId")
    private Collection<SisSchedule> sisSchedules = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "scId", referencedColumnName = "scId")
    private Collection<SisJoinCourse> sisJoinCourseList = new ArrayList<>();
}
