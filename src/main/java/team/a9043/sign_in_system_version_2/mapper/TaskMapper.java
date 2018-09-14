package team.a9043.sign_in_system_version_2.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskMapper {
    List<Map> getCozAttRate(
            @Param("curYear") Integer curYear,
            @Param("curTerm") Boolean curTerm);

    void updateCozAttRate(@Param("cozAttRateList") List<Map> cozAttRateList);
}
