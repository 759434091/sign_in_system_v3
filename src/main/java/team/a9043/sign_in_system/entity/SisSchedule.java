package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.DayOfWeek;

/**
 * @author a9043
 */
@Entity
@Getter
@Setter
public class SisSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Enum<ScFortnight> ssFortnight;
    @Column
    private DayOfWeek ssDayOfWeek;
    @Column
    private Integer ssStartTime;
    @Column
    private Integer ssEndTime;
    @ManyToOne
    @JoinColumn(name = "scId", referencedColumnName = "scId")
    private SisCourse sisCourse;

    enum ScFortnight {
        FULL(0), ODD(1), EVEN(2);

        private final int value;

        ScFortnight(int value) {
            this.value = value;
        }
    }
}
