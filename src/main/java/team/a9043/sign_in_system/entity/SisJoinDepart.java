package team.a9043.sign_in_system.entity;

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
public class SisJoinDepart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sjdId;


    @ManyToOne
    @JoinColumn(name = "scId", referencedColumnName = "scId")
    private SisCourse sisCourse;
    @ManyToOne
    @JoinColumn(name = "sdId", referencedColumnName = "sdId")
    private SisDepartment sisDepartment;
}
