package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 督导记录表实体
 * <p>
 * abbr: ssv
 *
 * @author a9043
 */
@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
public class SisSupervision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ssvId;
    @Column
    @NotNull
    private Integer ssvWeek;
    @Column
    @NotNull
    private Integer ssvActualNum;
    @Column
    @NotNull
    private Integer ssvMobileNum;
    @Column
    @NotNull
    private Integer ssvSleepNum;
    @Column(length = 150)
    @NotNull
    private String ssvRecInfo;
    @ManyToOne
    @JoinColumn(name = "ssId", referencedColumnName = "ssId")
    private SisSchedule sisSchedule;
}
