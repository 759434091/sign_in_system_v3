package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 参与课堂关联表
 * <p>
 * abbr: sjc
 *
 * @author a9043
 */
@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
public class SisJoinCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sjcId;
    /**
     * 0: ATTENDANCE
     * 1: TEACHING
     */
    @Column
    @Enumerated(EnumType.ORDINAL)
    private JoinCourseType joinCourseType;
    @ManyToOne
    @JoinColumn(name = "scId", referencedColumnName = "scId")
    private SisCourse sisCourse;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "suId", referencedColumnName = "suId")
    private SisUser sisUser;


    public enum JoinCourseType {
        ATTENDANCE(), TEACHING()
    }
}
