package team.a9043.sign_in_system.pojo;

public class SisRole {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_role.sr_id
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private Integer srId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_role.role_name
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private String roleName;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_role.sr_id
     *
     * @return the value of sis_role.sr_id
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Integer getSrId() {
        return srId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_role.sr_id
     *
     * @param srId the value for sis_role.sr_id
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSrId(Integer srId) {
        this.srId = srId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_role.role_name
     *
     * @return the value of sis_role.role_name
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_role.role_name
     *
     * @param roleName the value for sis_role.role_name
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }
}