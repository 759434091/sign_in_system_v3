package team.a9043.sign_in_system_version_2.service;

import org.springframework.stereotype.Service;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.mapper.TchMapper;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.User;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service("tchService")
public class TchService {
    @Resource
    private TchMapper tchMapper;

    public List<Course> showTchCourses(User user) throws ParameterNotFoundException {
        return tchMapper.selectCourseByTch(Optional
                .ofNullable(user.getUsrId())
                .filter(usrId -> !usrId.equals(""))
                .orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect user.usrId.")));
    }

}
