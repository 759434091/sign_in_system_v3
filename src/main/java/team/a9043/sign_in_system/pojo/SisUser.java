package team.a9043.sign_in_system.pojo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SisUser {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_user.su_id
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private String suId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_user.su_authorities_str
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private String suAuthoritiesStr;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_user.su_name
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private String suName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_user.su_openid
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private String suOpenid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_user.su_password
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private String suPassword;

    private String type;

    private String suiLackNum;

    public String getSuiLackNum() {
        return suiLackNum;
    }

    public void setSuiLackNum(String suiLackNum) {
        this.suiLackNum = suiLackNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_user.su_id
     *
     * @return the value of sis_user.su_id
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public String getSuId() {
        return suId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_user.su_id
     *
     * @param suId the value for sis_user.su_id
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSuId(String suId) {
        this.suId = suId == null ? null : suId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_user
     * .su_authorities_str
     *
     * @return the value of sis_user.su_authorities_str
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public String getSuAuthoritiesStr() {
        return suAuthoritiesStr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_user
     * .su_authorities_str
     *
     * @param suAuthoritiesStr the value for sis_user.su_authorities_str
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSuAuthoritiesStr(String suAuthoritiesStr) {
        this.suAuthoritiesStr = suAuthoritiesStr == null ? null :
            suAuthoritiesStr.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_user.su_name
     *
     * @return the value of sis_user.su_name
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public String getSuName() {
        return suName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_user.su_name
     *
     * @param suName the value for sis_user.su_name
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSuName(String suName) {
        this.suName = suName == null ? null : suName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_user.su_openid
     *
     * @return the value of sis_user.su_openid
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public String getSuOpenid() {
        return suOpenid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_user.su_openid
     *
     * @param suOpenid the value for sis_user.su_openid
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSuOpenid(String suOpenid) {
        this.suOpenid = suOpenid == null ? null : suOpenid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_user.su_password
     *
     * @return the value of sis_user.su_password
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public String getSuPassword() {
        return suPassword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_user.su_password
     *
     * @param suPassword the value for sis_user.su_password
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSuPassword(String suPassword) {
        this.suPassword = suPassword == null ? null : suPassword.trim();
    }

    public List<GrantedAuthority> getSuAuthorities() {
        if (null == suAuthoritiesStr) {
            return null;
        }
        return Arrays
            .stream(suAuthoritiesStr.split(","))
            .map(String::trim)
            .map(String::toUpperCase)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    public void setSuAuthorities(List<GrantedAuthority> suAuthorityList) {
        this.suAuthoritiesStr = String.join(",",
            suAuthorityList.stream().map(GrantedAuthority::getAuthority).toArray(String[]::new));
    }

    @Override
    public int hashCode() {
        return ("SisUser_" + suId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof SisUser))
            return false;
        return this.suId.equals(((SisUser) obj).getSuId());
    }
}