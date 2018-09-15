package team.a9043.sign_in_system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import team.a9043.sign_in_system.pojo.SisDepartment;
import team.a9043.sign_in_system.pojo.SisDepartmentExample;

public interface SisDepartmentMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    long countByExample(SisDepartmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    int deleteByExample(SisDepartmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    int deleteByPrimaryKey(Integer sdId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    int insert(SisDepartment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    int insertSelective(SisDepartment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    List<SisDepartment> selectByExample(SisDepartmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    SisDepartment selectByPrimaryKey(Integer sdId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    int updateByExampleSelective(@Param("record") SisDepartment record,
                                 @Param("example") SisDepartmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    int updateByExample(@Param("record") SisDepartment record, @Param(
        "example") SisDepartmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    int updateByPrimaryKeySelective(SisDepartment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sis_department
     *
     * @mbg.generated Sat Sep 01 22:13:45 CST 2018
     */
    int updateByPrimaryKey(SisDepartment record);

    int insertList(@Param("sisDepartmentList") List<SisDepartment> sisDepartmentList);
}