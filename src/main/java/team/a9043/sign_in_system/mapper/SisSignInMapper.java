package team.a9043.sign_in_system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import team.a9043.sign_in_system.pojo.SisSignIn;
import team.a9043.sign_in_system.pojo.SisSignInExample;

public interface SisSignInMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    long countByExample(SisSignInExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int deleteByExample(SisSignInExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int deleteByPrimaryKey(Integer ssiId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int insert(SisSignIn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int insertSelective(SisSignIn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    List<SisSignIn> selectByExample(SisSignInExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    SisSignIn selectByPrimaryKey(Integer ssiId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int updateByExampleSelective(@Param("record") SisSignIn record, @Param("example") SisSignInExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int updateByExample(@Param("record") SisSignIn record, @Param("example") SisSignInExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int updateByPrimaryKeySelective(SisSignIn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_sign_in
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int updateByPrimaryKey(SisSignIn record);
}