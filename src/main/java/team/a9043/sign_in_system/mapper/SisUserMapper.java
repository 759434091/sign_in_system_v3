package team.a9043.sign_in_system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.pojo.SisUserExample;

public interface SisUserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    long countByExample(SisUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int deleteByExample(SisUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int deleteByPrimaryKey(String suId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int insert(SisUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int insertSelective(SisUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    List<SisUser> selectByExample(SisUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    SisUser selectByPrimaryKey(String suId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int updateByExampleSelective(@Param("record") SisUser record, @Param(
        "example") SisUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int updateByExample(@Param("record") SisUser record,
                        @Param("example") SisUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int updateByPrimaryKeySelective(SisUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user
     *
     * @mbg.generated Mon Aug 27 13:08:41 CST 2018
     */
    int updateByPrimaryKey(SisUser record);

    int insertList(@Param("sisUserList") List<SisUser> sisUserList);
}