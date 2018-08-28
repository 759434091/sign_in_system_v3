package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 排课表实体
 * <p>
 * abbr: ss
 *
 * @author a9043
 */
@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
public class SisSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Integer ssId;
    /**
     * startYear-endYear-term
     * e.g: 2017-2018-2
     */
    @Column(length = 15)
    private String ssYearEtTerm;
    @Column
    private Integer ssStartWeek;
    @Column
    private Integer ssEndWeek;
    /**
     * 0: FULL
     * 1: ODD
     * 2: EVEN
     */
    @Column
    @Enumerated(EnumType.ORDINAL)
    private SsFortnight ssFortnight;
    @Column
    private DayOfWeek ssDayOfWeek;
    @Column
    private Integer ssStartTime;
    @Column
    private Integer ssEndTime;
    /**
     * 停课列表
     * <p>
     * integer
     * <p>
     * 分割符号: ","
     */
    @Column(length = 60)
    private String ssSuspension;

    /**
     * course
     */
    @ManyToOne
    @JoinColumn(name = "scId", referencedColumnName = "scId")
    private SisCourse sisCourse;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "ssId", referencedColumnName = "ssId")
    private Collection<SisSupervision> sisSupervisions = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "ssId", referencedColumnName = "ssId")
    private Collection<SisSignIn> sisSignIns = new ArrayList<>();

    public enum SsFortnight {
        FULL(), ODD(), EVEN();
    }

    public enum SsTerm {
        FIRST(), SECOND();

        /**
         * 学校系统转换
         *
         * @param value 学校系统值
         * @return enum
         */
        public static SsTerm toEnum(int value) {
            switch (value) {
                case 1:
                    return FIRST;
                case 2:
                    return SECOND;
                default:
                    return null;
            }
        }
    }
}
