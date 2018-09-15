package team.a9043.sign_in_system.pojo;

import javax.validation.constraints.NotBlank;

public class SisContact {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_contact.sct_id
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    private Integer sctId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_contact.sct_name
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    @NotBlank
    private String sctName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_contact.sct_contact
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    @NotBlank
    private String sctContact;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_contact.sct_content
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    @NotBlank
    private String sctContent;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_contact.sct_id
     *
     * @return the value of sis_contact.sct_id
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    public Integer getSctId() {
        return sctId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_contact.sct_id
     *
     * @param sctId the value for sis_contact.sct_id
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    public void setSctId(Integer sctId) {
        this.sctId = sctId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_contact.sct_name
     *
     * @return the value of sis_contact.sct_name
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    public String getSctName() {
        return sctName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_contact.sct_name
     *
     * @param sctName the value for sis_contact.sct_name
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    public void setSctName(String sctName) {
        this.sctName = sctName == null ? null : sctName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_contact.sct_contact
     *
     * @return the value of sis_contact.sct_contact
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    public String getSctContact() {
        return sctContact;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_contact.sct_contact
     *
     * @param sctContact the value for sis_contact.sct_contact
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    public void setSctContact(String sctContact) {
        this.sctContact = sctContact == null ? null : sctContact.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_contact.sct_content
     *
     * @return the value of sis_contact.sct_content
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    public String getSctContent() {
        return sctContent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_contact.sct_content
     *
     * @param sctContent the value for sis_contact.sct_content
     *
     * @mbg.generated Fri Sep 14 12:05:13 CST 2018
     */
    public void setSctContent(String sctContent) {
        this.sctContent = sctContent == null ? null : sctContent.trim();
    }
}