package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

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
    private Integer ssvActualNum;
    @Column
    private Integer ssvMobileNum;
    @Column
    private Integer ssvSleepNum;
    @Column(length = 150)
    private String ssvRecInfo;
    @ManyToOne
    @JoinColumn(name = "ssId", referencedColumnName = "ssId")
    private SisSchedule sisSchedule;
}
