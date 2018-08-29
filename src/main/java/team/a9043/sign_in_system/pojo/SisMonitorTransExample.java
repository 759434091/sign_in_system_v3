package team.a9043.sign_in_system.pojo;

import java.util.ArrayList;
import java.util.List;

public class SisMonitorTransExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public SisMonitorTransExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andSsIdIsNull() {
            addCriterion("ss_id is null");
            return (Criteria) this;
        }

        public Criteria andSsIdIsNotNull() {
            addCriterion("ss_id is not null");
            return (Criteria) this;
        }

        public Criteria andSsIdEqualTo(Integer value) {
            addCriterion("ss_id =", value, "ssId");
            return (Criteria) this;
        }

        public Criteria andSsIdNotEqualTo(Integer value) {
            addCriterion("ss_id <>", value, "ssId");
            return (Criteria) this;
        }

        public Criteria andSsIdGreaterThan(Integer value) {
            addCriterion("ss_id >", value, "ssId");
            return (Criteria) this;
        }

        public Criteria andSsIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("ss_id >=", value, "ssId");
            return (Criteria) this;
        }

        public Criteria andSsIdLessThan(Integer value) {
            addCriterion("ss_id <", value, "ssId");
            return (Criteria) this;
        }

        public Criteria andSsIdLessThanOrEqualTo(Integer value) {
            addCriterion("ss_id <=", value, "ssId");
            return (Criteria) this;
        }

        public Criteria andSsIdIn(List<Integer> values) {
            addCriterion("ss_id in", values, "ssId");
            return (Criteria) this;
        }

        public Criteria andSsIdNotIn(List<Integer> values) {
            addCriterion("ss_id not in", values, "ssId");
            return (Criteria) this;
        }

        public Criteria andSsIdBetween(Integer value1, Integer value2) {
            addCriterion("ss_id between", value1, value2, "ssId");
            return (Criteria) this;
        }

        public Criteria andSsIdNotBetween(Integer value1, Integer value2) {
            addCriterion("ss_id not between", value1, value2, "ssId");
            return (Criteria) this;
        }

        public Criteria andSmtWeekIsNull() {
            addCriterion("smt_week is null");
            return (Criteria) this;
        }

        public Criteria andSmtWeekIsNotNull() {
            addCriterion("smt_week is not null");
            return (Criteria) this;
        }

        public Criteria andSmtWeekEqualTo(Integer value) {
            addCriterion("smt_week =", value, "smtWeek");
            return (Criteria) this;
        }

        public Criteria andSmtWeekNotEqualTo(Integer value) {
            addCriterion("smt_week <>", value, "smtWeek");
            return (Criteria) this;
        }

        public Criteria andSmtWeekGreaterThan(Integer value) {
            addCriterion("smt_week >", value, "smtWeek");
            return (Criteria) this;
        }

        public Criteria andSmtWeekGreaterThanOrEqualTo(Integer value) {
            addCriterion("smt_week >=", value, "smtWeek");
            return (Criteria) this;
        }

        public Criteria andSmtWeekLessThan(Integer value) {
            addCriterion("smt_week <", value, "smtWeek");
            return (Criteria) this;
        }

        public Criteria andSmtWeekLessThanOrEqualTo(Integer value) {
            addCriterion("smt_week <=", value, "smtWeek");
            return (Criteria) this;
        }

        public Criteria andSmtWeekIn(List<Integer> values) {
            addCriterion("smt_week in", values, "smtWeek");
            return (Criteria) this;
        }

        public Criteria andSmtWeekNotIn(List<Integer> values) {
            addCriterion("smt_week not in", values, "smtWeek");
            return (Criteria) this;
        }

        public Criteria andSmtWeekBetween(Integer value1, Integer value2) {
            addCriterion("smt_week between", value1, value2, "smtWeek");
            return (Criteria) this;
        }

        public Criteria andSmtWeekNotBetween(Integer value1, Integer value2) {
            addCriterion("smt_week not between", value1, value2, "smtWeek");
            return (Criteria) this;
        }

        public Criteria andSuIdIsNull() {
            addCriterion("su_id is null");
            return (Criteria) this;
        }

        public Criteria andSuIdIsNotNull() {
            addCriterion("su_id is not null");
            return (Criteria) this;
        }

        public Criteria andSuIdEqualTo(String value) {
            addCriterion("su_id =", value, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdNotEqualTo(String value) {
            addCriterion("su_id <>", value, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdGreaterThan(String value) {
            addCriterion("su_id >", value, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdGreaterThanOrEqualTo(String value) {
            addCriterion("su_id >=", value, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdLessThan(String value) {
            addCriterion("su_id <", value, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdLessThanOrEqualTo(String value) {
            addCriterion("su_id <=", value, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdLike(String value) {
            addCriterion("su_id like", value, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdNotLike(String value) {
            addCriterion("su_id not like", value, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdIn(List<String> values) {
            addCriterion("su_id in", values, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdNotIn(List<String> values) {
            addCriterion("su_id not in", values, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdBetween(String value1, String value2) {
            addCriterion("su_id between", value1, value2, "suId");
            return (Criteria) this;
        }

        public Criteria andSuIdNotBetween(String value1, String value2) {
            addCriterion("su_id not between", value1, value2, "suId");
            return (Criteria) this;
        }

        public Criteria andSmtStatusIsNull() {
            addCriterion("smt_status is null");
            return (Criteria) this;
        }

        public Criteria andSmtStatusIsNotNull() {
            addCriterion("smt_status is not null");
            return (Criteria) this;
        }

        public Criteria andSmtStatusEqualTo(Integer value) {
            addCriterion("smt_status =", value, "smtStatus");
            return (Criteria) this;
        }

        public Criteria andSmtStatusNotEqualTo(Integer value) {
            addCriterion("smt_status <>", value, "smtStatus");
            return (Criteria) this;
        }

        public Criteria andSmtStatusGreaterThan(Integer value) {
            addCriterion("smt_status >", value, "smtStatus");
            return (Criteria) this;
        }

        public Criteria andSmtStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("smt_status >=", value, "smtStatus");
            return (Criteria) this;
        }

        public Criteria andSmtStatusLessThan(Integer value) {
            addCriterion("smt_status <", value, "smtStatus");
            return (Criteria) this;
        }

        public Criteria andSmtStatusLessThanOrEqualTo(Integer value) {
            addCriterion("smt_status <=", value, "smtStatus");
            return (Criteria) this;
        }

        public Criteria andSmtStatusIn(List<Integer> values) {
            addCriterion("smt_status in", values, "smtStatus");
            return (Criteria) this;
        }

        public Criteria andSmtStatusNotIn(List<Integer> values) {
            addCriterion("smt_status not in", values, "smtStatus");
            return (Criteria) this;
        }

        public Criteria andSmtStatusBetween(Integer value1, Integer value2) {
            addCriterion("smt_status between", value1, value2, "smtStatus");
            return (Criteria) this;
        }

        public Criteria andSmtStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("smt_status not between", value1, value2, "smtStatus");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated do_not_delete_during_merge Tue Aug 28 21:51:04 CST 2018
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table sis_monitor_trans
     *
     * @mbg.generated Tue Aug 28 21:51:04 CST 2018
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}