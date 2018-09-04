package team.a9043.sign_in_system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.mapper.SisUserMapper;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.pojo.SisUserExample;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentManageService {
    @Resource
    private SisUserMapper sisUserMapper;

    public JSONObject getStudents(@Nullable Integer page,
                                  @Nullable Integer pageSize,
                                  @Nullable String suId,
                                  @Nullable String suName) throws IncorrectParameterException {
        if (null == page) {
            throw new IncorrectParameterException("Incorrect page: " + null);
        }
        if (page < 1)
            throw new IncorrectParameterException("Incorrect page: " + page +
                " (must equal or bigger than 1)");
        if (null == pageSize)
            pageSize = 10;
        else if (pageSize <= 0 || pageSize > 500) {
            throw new IncorrectParameterException("pageSize must between [1,500]");
        }


        PageHelper.startPage(page, pageSize);
        SisUserExample sisUserExample = new SisUserExample();
        SisUserExample.Criteria criteria = sisUserExample.createCriteria();
        criteria.andSuAuthoritiesStrLike("%STUDENT%");

        if (null != suId) {
            criteria.andSuIdLike("%" + suId + "%");
        }
        if (null != suName) {
            criteria.andSuNameLike(CourseService.getFuzzySearch(suName));
        }

        List<SisUser> sisUserList = sisUserMapper.selectByExample(sisUserExample);
        PageInfo<SisUser> pageInfo = new PageInfo<>(sisUserList);

        List<String> suIdList = sisUserList.parallelStream()
            .map(SisUser::getSuId)
            .distinct()
            .collect(Collectors.toList());

        if (suIdList.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("page", page);
            jsonObject.put("message", "No student");
            return jsonObject;
        }

        JSONObject pageJson = new JSONObject(pageInfo);
        pageJson.getJSONArray("list")
            .forEach(sisUserObj -> {
                JSONObject sisUserJson = (JSONObject) sisUserObj;

                sisUserJson.remove("suPassword");
            });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("page", page);
        jsonObject.put("data", pageJson);
        return jsonObject;
    }
}
