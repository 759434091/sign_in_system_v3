package team.a9043.sign_in_system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import team.a9043.sign_in_system.pojo.SisLocation;
import team.a9043.sign_in_system.pojo.SisLocationExample;

public interface SisLocationMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    long countByExample(SisLocationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    int deleteByExample(SisLocationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    int deleteByPrimaryKey(Integer slId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    int insert(SisLocation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    int insertSelective(SisLocation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    List<SisLocation> selectByExample(SisLocationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    SisLocation selectByPrimaryKey(Integer slId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    int updateByExampleSelective(@Param("record") SisLocation record, @Param(
        "example") SisLocationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    int updateByExample(@Param("record") SisLocation record,
                        @Param("example") SisLocationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    int updateByPrimaryKeySelective(SisLocation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_location
     *
     * @mbg.generated Sat Sep 01 21:38:31 CST 2018
     */
    int updateByPrimaryKey(SisLocation record);

    int insertList(@Param("sisLocationList") List<SisLocation> sisLocationList);
}