package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
public class SisUserInfo implements Serializable {
    @Id
    @NotNull
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "suId", referencedColumnName = "suId")
    private SisUser suId;

    @Column(length = 20)
    @NotNull
    private Integer suiLackNum = 0;
}
