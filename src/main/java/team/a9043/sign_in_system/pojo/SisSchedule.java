package team.a9043.sign_in_system.pojo;

public class SisSchedule {
    public enum SsFortnight {
        FULL(), ODD(), EVEN();

        public static SisSchedule.SsFortnight valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }

    public enum SsTerm {
        FIRST(), SECOND();

        /**
         * 学校系统转换
         *
         * @param value 学校系统值
         * @return enum
         */
        public static SisSchedule.SsTerm toEnum(int value) {
            switch (value) {
                case 1:
                    return FIRST;
                case 2:
                    return SECOND;
                default:
                    return null;
            }
        }
    }

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule.ss_id
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private Integer ssId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule.ss_day_of_week
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private Integer ssDayOfWeek;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule.ss_end_time
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private Integer ssEndTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule.ss_end_week
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private Integer ssEndWeek;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule.ss_fortnight
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private Integer ssFortnight;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule.ss_start_time
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private Integer ssStartTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule.ss_start_week
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private Integer ssStartWeek;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule
     * .ss_year_et_term
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private String ssYearEtTerm;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule.sc_id
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private String scId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule.ss_suspension
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private String ssSuspension;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule
     * .ss_need_monitor
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private Boolean ssNeedMonitor;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sis_schedule.su_id
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    private String suId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule.ss_id
     *
     * @return the value of sis_schedule.ss_id
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Integer getSsId() {
        return ssId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule.ss_id
     *
     * @param ssId the value for sis_schedule.ss_id
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsId(Integer ssId) {
        this.ssId = ssId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule
     * .ss_day_of_week
     *
     * @return the value of sis_schedule.ss_day_of_week
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Integer getSsDayOfWeek() {
        return ssDayOfWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule
     * .ss_day_of_week
     *
     * @param ssDayOfWeek the value for sis_schedule.ss_day_of_week
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsDayOfWeek(Integer ssDayOfWeek) {
        this.ssDayOfWeek = ssDayOfWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule
     * .ss_end_time
     *
     * @return the value of sis_schedule.ss_end_time
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Integer getSsEndTime() {
        return ssEndTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule
     * .ss_end_time
     *
     * @param ssEndTime the value for sis_schedule.ss_end_time
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsEndTime(Integer ssEndTime) {
        this.ssEndTime = ssEndTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule
     * .ss_end_week
     *
     * @return the value of sis_schedule.ss_end_week
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Integer getSsEndWeek() {
        return ssEndWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule
     * .ss_end_week
     *
     * @param ssEndWeek the value for sis_schedule.ss_end_week
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsEndWeek(Integer ssEndWeek) {
        this.ssEndWeek = ssEndWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule
     * .ss_fortnight
     *
     * @return the value of sis_schedule.ss_fortnight
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Integer getSsFortnight() {
        return ssFortnight;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule
     * .ss_fortnight
     *
     * @param ssFortnight the value for sis_schedule.ss_fortnight
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsFortnight(Integer ssFortnight) {
        this.ssFortnight = ssFortnight;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule
     * .ss_start_time
     *
     * @return the value of sis_schedule.ss_start_time
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Integer getSsStartTime() {
        return ssStartTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule
     * .ss_start_time
     *
     * @param ssStartTime the value for sis_schedule.ss_start_time
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsStartTime(Integer ssStartTime) {
        this.ssStartTime = ssStartTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule
     * .ss_start_week
     *
     * @return the value of sis_schedule.ss_start_week
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Integer getSsStartWeek() {
        return ssStartWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule
     * .ss_start_week
     *
     * @param ssStartWeek the value for sis_schedule.ss_start_week
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsStartWeek(Integer ssStartWeek) {
        this.ssStartWeek = ssStartWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule
     * .ss_year_et_term
     *
     * @return the value of sis_schedule.ss_year_et_term
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public String getSsYearEtTerm() {
        return ssYearEtTerm;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule
     * .ss_year_et_term
     *
     * @param ssYearEtTerm the value for sis_schedule.ss_year_et_term
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsYearEtTerm(String ssYearEtTerm) {
        this.ssYearEtTerm = ssYearEtTerm == null ? null : ssYearEtTerm.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule.sc_id
     *
     * @return the value of sis_schedule.sc_id
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public String getScId() {
        return scId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule.sc_id
     *
     * @param scId the value for sis_schedule.sc_id
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setScId(String scId) {
        this.scId = scId == null ? null : scId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule
     * .ss_suspension
     *
     * @return the value of sis_schedule.ss_suspension
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public String getSsSuspension() {
        return ssSuspension;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule
     * .ss_suspension
     *
     * @param ssSuspension the value for sis_schedule.ss_suspension
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsSuspension(String ssSuspension) {
        this.ssSuspension = ssSuspension == null ? null : ssSuspension.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule
     * .ss_need_monitor
     *
     * @return the value of sis_schedule.ss_need_monitor
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public Boolean getSsNeedMonitor() {
        return ssNeedMonitor;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule
     * .ss_need_monitor
     *
     * @param ssNeedMonitor the value for sis_schedule.ss_need_monitor
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSsNeedMonitor(Boolean ssNeedMonitor) {
        this.ssNeedMonitor = ssNeedMonitor;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sis_schedule.su_id
     *
     * @return the value of sis_schedule.su_id
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public String getSuId() {
        return suId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sis_schedule.su_id
     *
     * @param suId the value for sis_schedule.su_id
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    public void setSuId(String suId) {
        this.suId = suId == null ? null : suId.trim();
    }
}