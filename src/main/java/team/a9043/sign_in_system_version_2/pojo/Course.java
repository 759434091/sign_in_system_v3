package team.a9043.sign_in_system_version_2.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column course.coz_id
     *
     * @mbg.generated
     */
    private String cozId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column course.coz_name
     *
     * @mbg.generated
     */
    private String cozName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column course.coz_size
     *
     * @mbg.generated
     */
    private Integer cozSize;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column course.coz_act_size
     *
     * @mbg.generated
     */
    private Integer cozActSize;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column course.coz_att_rate
     *
     * @mbg.generated
     */
    private BigDecimal cozAttRate;

    private Integer cozGrade;

    private List<User> teacherList;

    private List<Schedule> scheduleList;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column course.coz_id
     *
     * @return the value of course.coz_id
     * @mbg.generated
     */
    public String getCozId() {
        return cozId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column course.coz_id
     *
     * @param cozId the value for course.coz_id
     * @mbg.generated
     */
    public void setCozId(String cozId) {
        this.cozId = cozId == null ? null : cozId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column course.coz_name
     *
     * @return the value of course.coz_name
     * @mbg.generated
     */
    public String getCozName() {
        return cozName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column course.coz_name
     *
     * @param cozName the value for course.coz_name
     * @mbg.generated
     */
    public void setCozName(String cozName) {
        this.cozName = cozName == null ? null : cozName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column course.coz_size
     *
     * @return the value of course.coz_size
     * @mbg.generated
     */
    public Integer getCozSize() {
        return cozSize;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column course.coz_size
     *
     * @param cozSize the value for course.coz_size
     * @mbg.generated
     */
    public void setCozSize(Integer cozSize) {
        this.cozSize = cozSize;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column course.coz_act_size
     *
     * @return the value of course.coz_act_size
     * @mbg.generated
     */
    public Integer getCozActSize() {
        return cozActSize;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column course.coz_act_size
     *
     * @param cozActSize the value for course.coz_act_size
     * @mbg.generated
     */
    public void setCozActSize(Integer cozActSize) {
        this.cozActSize = cozActSize;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column course.coz_att_rate
     *
     * @return the value of course.coz_att_rate
     * @mbg.generated
     */
    public BigDecimal getCozAttRate() {
        return cozAttRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column course.coz_att_rate
     *
     * @param cozAttRate the value for course.coz_att_rate
     * @mbg.generated
     */
    public void setCozAttRate(BigDecimal cozAttRate) {
        this.cozAttRate = cozAttRate;
    }

    public List<User> getTeacherList() {
        return teacherList;
    }

    public Course setTeacherList(List<User> teacherList) {
        this.teacherList = teacherList;
        return this;
    }

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public Course setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
        return this;
    }

    public Integer getCozGrade() {
        return cozGrade;
    }

    public Course setCozGrade(Integer cozGrade) {
        this.cozGrade = cozGrade;
        return this;
    }
}