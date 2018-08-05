package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 参与课堂关联表
 * <p>
 * abbr: sj
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
    private Integer sjId;
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
    @ManyToOne
    @JoinColumn(name = "suId", referencedColumnName = "suId")
    private SisUser sisUser;


    enum JoinCourseType {
        ATTENDANCE(0), TEACHING(1);

        private final int value;

        JoinCourseType(int value) {
            this.value = value;
        }
    }
}
