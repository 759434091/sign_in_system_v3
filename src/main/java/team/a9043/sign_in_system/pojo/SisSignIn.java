package team.a9043.sign_in_system.pojo;

import java.time.LocalDateTime;

public class SisSignIn {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_sign_in.ssi_id
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    private Integer ssiId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_sign_in.ss_id
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    private Integer ssId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_sign_in.ssi_week
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    private Integer ssiWeek;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_sign_in.ssi_create_time
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    private LocalDateTime ssiCreateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_sign_in.ssi_id
     *
     * @return the value of sis_sign_in.ssi_id
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    public Integer getSsiId() {
        return ssiId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_sign_in.ssi_id
     *
     * @param ssiId the value for sis_sign_in.ssi_id
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    public void setSsiId(Integer ssiId) {
        this.ssiId = ssiId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_sign_in.ss_id
     *
     * @return the value of sis_sign_in.ss_id
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    public Integer getSsId() {
        return ssId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_sign_in.ss_id
     *
     * @param ssId the value for sis_sign_in.ss_id
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    public void setSsId(Integer ssId) {
        this.ssId = ssId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_sign_in.ssi_week
     *
     * @return the value of sis_sign_in.ssi_week
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    public Integer getSsiWeek() {
        return ssiWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_sign_in.ssi_week
     *
     * @param ssiWeek the value for sis_sign_in.ssi_week
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    public void setSsiWeek(Integer ssiWeek) {
        this.ssiWeek = ssiWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_sign_in.ssi_create_time
     *
     * @return the value of sis_sign_in.ssi_create_time
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    public LocalDateTime getSsiCreateTime() {
        return ssiCreateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_sign_in.ssi_create_time
     *
     * @param ssiCreateTime the value for sis_sign_in.ssi_create_time
     *
     * @mbg.generated Tue Aug 28 12:23:09 CST 2018
     */
    public void setSsiCreateTime(LocalDateTime ssiCreateTime) {
        this.ssiCreateTime = ssiCreateTime;
    }
}