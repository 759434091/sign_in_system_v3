package team.a9043.sign_in_system.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
@ApiModel("督导记录")
public class SisSupervision {
    @EmbeddedId
    @ApiModelProperty(value = "主键列", notes = "联合主键")
    @NotNull
    private IdClass ssvId;
    @Column
    @NotNull
    @ApiModelProperty("实际上课人数")
    private Integer ssvActualNum;
    @Column
    @NotNull
    @ApiModelProperty("玩手机人数")
    private Integer ssvMobileNum;
    @Column
    @NotNull
    @ApiModelProperty("睡觉人数")
    private Integer ssvSleepNum;
    @Column(length = 150)
    @NotNull
    @ApiModelProperty(value = "督导详情记录", notes = "长度不超过150字")
    private String ssvRecInfo;


    @Embeddable
    @Getter
    @Setter
    public static class IdClass implements Serializable {
        @ManyToOne
        @ApiModelProperty("督导课程排课")
        @JoinColumn(name = "ssId", referencedColumnName = "ssId")
        private SisSchedule sisSchedule;

        @Column
        @NotNull
        @ApiModelProperty("督导周")
        private Integer ssvWeek;
    }
}
