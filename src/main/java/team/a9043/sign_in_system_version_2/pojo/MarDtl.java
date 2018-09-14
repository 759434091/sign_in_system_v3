package team.a9043.sign_in_system_version_2.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarDtl {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mar_dtl_2017_true.mard_id
     *
     * @mbg.generated
     */
    private Integer mardId;

    private User user;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mar_dtl_2017_true.man_abs_rec_2017_true_mar_id
     *
     * @mbg.generated
     */
    private Integer manAbsRecMarId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mar_dtl_2017_true.mard_id
     *
     * @return the value of mar_dtl_2017_true.mard_id
     * @mbg.generated
     */
    public Integer getMardId() {
        return mardId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mar_dtl_2017_true.mard_id
     *
     * @param mardId the value for mar_dtl_2017_true.mard_id
     * @mbg.generated
     */
    public void setMardId(Integer mardId) {
        this.mardId = mardId;
    }

    public User getUser() {
        return user;
    }

    public MarDtl setUser(User user) {
        this.user = user;
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mar_dtl_2017_true.man_abs_rec_2017_true_mar_id
     *
     * @return the value of mar_dtl_2017_true.man_abs_rec_2017_true_mar_id
     * @mbg.generated
     */
    public Integer getManAbsRecMarId() {
        return manAbsRecMarId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mar_dtl_2017_true.man_abs_rec_2017_true_mar_id
     *
     * @param manAbsRecMarId the value for mar_dtl_2017_true.man_abs_rec_2017_true_mar_id
     * @mbg.generated
     */
    public void setManAbsRecMarId(Integer manAbsRecMarId) {
        this.manAbsRecMarId = manAbsRecMarId;
    }
}