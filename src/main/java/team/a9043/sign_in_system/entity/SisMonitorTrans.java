package team.a9043.sign_in_system.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import team.a9043.sign_in_system.exception.String2EnumException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author a9043
 */
@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@ApiModel("督导转接")
public class SisMonitorTrans {
    @EmbeddedId
    @ApiModelProperty(value = "主键列", notes = "联合主键")
    @NotNull
    private IdClass smtId;

    /**
     * 0: UNTREATED
     * 1: AGREE
     * 2: DISAGREE
     */
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private SisMonitorTrans.SmtStatus smtStatus;

    @ManyToOne
    @JoinColumn(name = "suId", referencedColumnName = "suId")
    @NotNull
    private SisUser sisUser;


    public enum SmtStatus {
        UNTREATED(0), AGREE(1), DISAGREE(2);

        private final int value;

        SmtStatus(int value) {
            this.value = value;
        }

        public static SmtStatus lowercase2Enum(String value) throws String2EnumException {
            switch (value) {
                case "untreated":
                    return UNTREATED;
                case "agree":
                    return AGREE;
                case "disagree":
                    return DISAGREE;
                default:
                    throw new String2EnumException("No enum: " + value);
            }
        }
    }

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
        @ApiModelProperty("转接周")
        private Integer smtWeek;

        @Override
        public String toString() {
            return String.format("ssId %d, smtWeek %d",
                sisSchedule.getSsId()
                , smtWeek);
        }
    }
}
