package team.a9043.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import team.a9043.pojo.Schedule;

import java.util.List;

@Repository
public interface AspectMapper {
    Schedule findSchCoz(@Param("schId") int schId);

    List<Schedule> findCozSch(@Param("cozId") String cozId);
}
