package team.a9043.sign_in_system_version_2.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInRec {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sign_in_rec_2017_true.sir_id
     *
     * @mbg.generated
     */
    private Integer sirId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sign_in_rec_2017_true.sir_time
     *
     * @mbg.generated
     */
    private LocalDateTime sirTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sign_in_rec_2017_true.sir_leave
     *
     * @mbg.generated
     */
    private Boolean sirLeave;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sign_in_rec_2017_true.sir_approve
     *
     * @mbg.generated
     */
    private Integer sirApprove;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sign_in_rec_2017_true.sign_in_2017_true_si_id
     *
     * @mbg.generated
     */
    private Integer signInSiId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sign_in_rec_2017_true.user_usr_id
     *
     * @mbg.generated
     */
    private String userUsrId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sign_in_rec_2017_true.sir_voucher
     *
     * @mbg.generated
     */
    private byte[] sirVoucher;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sign_in_rec_2017_true.sir_id
     *
     * @return the value of sign_in_rec_2017_true.sir_id
     * @mbg.generated
     */
    public Integer getSirId() {
        return sirId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sign_in_rec_2017_true.sir_id
     *
     * @param sirId the value for sign_in_rec_2017_true.sir_id
     * @mbg.generated
     */
    public void setSirId(Integer sirId) {
        this.sirId = sirId;
    }

    public LocalDateTime getSirTime() {
        return sirTime;
    }

    public SignInRec setSirTime(LocalDateTime sirTime) {
        this.sirTime = sirTime;
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sign_in_rec_2017_true.sir_leave
     *
     * @return the value of sign_in_rec_2017_true.sir_leave
     * @mbg.generated
     */
    public Boolean getSirLeave() {
        return sirLeave;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sign_in_rec_2017_true.sir_leave
     *
     * @param sirLeave the value for sign_in_rec_2017_true.sir_leave
     * @mbg.generated
     */
    public void setSirLeave(Boolean sirLeave) {
        this.sirLeave = sirLeave;
    }

    public Integer getSirApprove() {
        return sirApprove;
    }

    public SignInRec setSirApprove(Integer sirApprove) {
        this.sirApprove = sirApprove;
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sign_in_rec_2017_true.sign_in_2017_true_si_id
     *
     * @return the value of sign_in_rec_2017_true.sign_in_2017_true_si_id
     * @mbg.generated
     */
    public Integer getSignInSiId() {
        return signInSiId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sign_in_rec_2017_true.sign_in_2017_true_si_id
     *
     * @param signInSiId the value for sign_in_rec_2017_true.sign_in_2017_true_si_id
     * @mbg.generated
     */
    public void setSignInSiId(Integer signInSiId) {
        this.signInSiId = signInSiId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sign_in_rec_2017_true.user_usr_id
     *
     * @return the value of sign_in_rec_2017_true.user_usr_id
     * @mbg.generated
     */
    public String getUserUsrId() {
        return userUsrId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sign_in_rec_2017_true.user_usr_id
     *
     * @param userUsrId the value for sign_in_rec_2017_true.user_usr_id
     * @mbg.generated
     */
    public void setUserUsrId(String userUsrId) {
        this.userUsrId = userUsrId == null ? null : userUsrId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sign_in_rec_2017_true.sir_voucher
     *
     * @return the value of sign_in_rec_2017_true.sir_voucher
     * @mbg.generated
     */
    public byte[] getSirVoucher() {
        return sirVoucher;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sign_in_rec_2017_true.sir_voucher
     *
     * @param sirVoucher the value for sign_in_rec_2017_true.sir_voucher
     * @mbg.generated
     */
    public void setSirVoucher(byte[] sirVoucher) {
        this.sirVoucher = sirVoucher;
    }
}