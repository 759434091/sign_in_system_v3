package team.a9043.sign_in_system.pojo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class SisSupervisionKey {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_supervision.ss_id
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    @ApiModelProperty("督导课程排课")
    private Integer ssId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_supervision.ssv_week
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    @NotNull
    @ApiModelProperty("督导周")
    private Integer ssvWeek;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_supervision.ss_id
     *
     * @return the value of sis_supervision.ss_id
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Integer getSsId() {
        return ssId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_supervision.ss_id
     *
     * @param ssId the value for sis_supervision.ss_id
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsId(Integer ssId) {
        this.ssId = ssId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_supervision.ssv_week
     *
     * @return the value of sis_supervision.ssv_week
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Integer getSsvWeek() {
        return ssvWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_supervision.ssv_week
     *
     * @param ssvWeek the value for sis_supervision.ssv_week
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsvWeek(Integer ssvWeek) {
        this.ssvWeek = ssvWeek;
    }
}