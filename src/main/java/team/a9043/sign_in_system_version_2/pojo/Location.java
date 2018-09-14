package team.a9043.sign_in_system_version_2.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Location {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column location.loc_id
     *
     * @mbg.generated
     */
    private Integer locId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column location.loc_name
     *
     * @mbg.generated
     */
    private String locName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column location.loc_long
     *
     * @mbg.generated
     */
    private Double locLong;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column location.loc_lat
     *
     * @mbg.generated
     */
    private Double locLat;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column location.loc_id
     *
     * @return the value of location.loc_id
     *
     * @mbg.generated
     */
    public Integer getLocId() {
        return locId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column location.loc_id
     *
     * @param locId the value for location.loc_id
     *
     * @mbg.generated
     */
    public void setLocId(Integer locId) {
        this.locId = locId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column location.loc_name
     *
     * @return the value of location.loc_name
     *
     * @mbg.generated
     */
    public String getLocName() {
        return locName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column location.loc_name
     *
     * @param locName the value for location.loc_name
     *
     * @mbg.generated
     */
    public void setLocName(String locName) {
        this.locName = locName == null ? null : locName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column location.loc_long
     *
     * @return the value of location.loc_long
     *
     * @mbg.generated
     */
    public Double getLocLong() {
        return locLong;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column location.loc_long
     *
     * @param locLong the value for location.loc_long
     *
     * @mbg.generated
     */
    public void setLocLong(Double locLong) {
        this.locLong = locLong;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column location.loc_lat
     *
     * @return the value of location.loc_lat
     *
     * @mbg.generated
     */
    public Double getLocLat() {
        return locLat;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column location.loc_lat
     *
     * @param locLat the value for location.loc_lat
     *
     * @mbg.generated
     */
    public void setLocLat(Double locLat) {
        this.locLat = locLat;
    }
}