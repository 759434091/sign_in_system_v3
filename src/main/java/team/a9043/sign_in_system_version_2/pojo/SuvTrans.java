package team.a9043.sign_in_system_version_2.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuvTrans {

    private Supervision supervision;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column suv_trans_2017_true.user_usr_id
     *
     * @mbg.generated
     */
    private String userUsrId;

    public Supervision getSupervision() {
        return supervision;
    }

    public SuvTrans setSupervision(Supervision supervision) {
        this.supervision = supervision;
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column suv_trans_2017_true.user_usr_id
     *
     * @return the value of suv_trans_2017_true.user_usr_id
     * @mbg.generated
     */
    public String getUserUsrId() {
        return userUsrId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column suv_trans_2017_true.user_usr_id
     *
     * @param userUsrId the value for suv_trans_2017_true.user_usr_id
     * @mbg.generated
     */
    public void setUserUsrId(String userUsrId) {
        this.userUsrId = userUsrId == null ? null : userUsrId.trim();
    }
}