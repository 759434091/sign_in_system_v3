package team.a9043.sign_in_system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import team.a9043.sign_in_system.pojo.SisUserInfo;
import team.a9043.sign_in_system.pojo.SisUserInfoExample;

public interface SisUserInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    long countByExample(SisUserInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    int deleteByExample(SisUserInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    int deleteByPrimaryKey(String suId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    int insert(SisUserInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    int insertSelective(SisUserInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    List<SisUserInfo> selectByExample(SisUserInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    SisUserInfo selectByPrimaryKey(String suId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    int updateByExampleSelective(@Param("record") SisUserInfo record, @Param("example") SisUserInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    int updateByExample(@Param("record") SisUserInfo record, @Param("example") SisUserInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    int updateByPrimaryKeySelective(SisUserInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_user_info
     *
     * @mbg.generated Sun Oct 07 22:29:39 CST 2018
     */
    int updateByPrimaryKey(SisUserInfo record);


    int insertList(@Param("sisUserInfoList") List<SisUserInfo> sisUserInfoList);
}