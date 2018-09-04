package team.a9043.sign_in_system.pojo;

import java.util.ArrayList;
import java.util.List;

public class SisUserInfoExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    public SisUserInfoExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
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
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
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

        public Criteria andLackNumIsNull() {
            addCriterion("lack_num is null");
            return (Criteria) this;
        }

        public Criteria andLackNumIsNotNull() {
            addCriterion("lack_num is not null");
            return (Criteria) this;
        }

        public Criteria andLackNumEqualTo(Integer value) {
            addCriterion("lack_num =", value, "suiLackNum");
            return (Criteria) this;
        }

        public Criteria andLackNumNotEqualTo(Integer value) {
            addCriterion("lack_num <>", value, "suiLackNum");
            return (Criteria) this;
        }

        public Criteria andLackNumGreaterThan(Integer value) {
            addCriterion("lack_num >", value, "suiLackNum");
            return (Criteria) this;
        }

        public Criteria andLackNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("lack_num >=", value, "suiLackNum");
            return (Criteria) this;
        }

        public Criteria andLackNumLessThan(Integer value) {
            addCriterion("lack_num <", value, "suiLackNum");
            return (Criteria) this;
        }

        public Criteria andLackNumLessThanOrEqualTo(Integer value) {
            addCriterion("lack_num <=", value, "suiLackNum");
            return (Criteria) this;
        }

        public Criteria andLackNumIn(List<Integer> values) {
            addCriterion("lack_num in", values, "suiLackNum");
            return (Criteria) this;
        }

        public Criteria andLackNumNotIn(List<Integer> values) {
            addCriterion("lack_num not in", values, "suiLackNum");
            return (Criteria) this;
        }

        public Criteria andLackNumBetween(Integer value1, Integer value2) {
            addCriterion("lack_num between", value1, value2, "suiLackNum");
            return (Criteria) this;
        }

        public Criteria andLackNumNotBetween(Integer value1, Integer value2) {
            addCriterion("lack_num not between", value1, value2, "suiLackNum");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table sis_user_info
     *
     * @mbg.generated do_not_delete_during_merge Tue Sep 04 13:25:29 CST 2018
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table sis_user_info
     *
     * @mbg.generated Tue Sep 04 13:25:29 CST 2018
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