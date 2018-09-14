package team.a9043.sign_in_system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.mapper.SisLocationMapper;
import team.a9043.sign_in_system.mapper.SisScheduleMapper;
import team.a9043.sign_in_system.pojo.SisLocation;
import team.a9043.sign_in_system.pojo.SisLocationExample;
import team.a9043.sign_in_system.pojo.SisSchedule;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author a9043
 */
@Service
@Slf4j
public class LocationService {
    @Resource
    private SisLocationMapper sisLocationMapper;
    @Resource
    private SisScheduleMapper sisScheduleMapper;

    @Transactional
    public JSONObject modifyScheduleLocation(Integer ssId, Integer slId) throws IncorrectParameterException {
        SisSchedule sisSchedule = sisScheduleMapper.selectByPrimaryKey(ssId);
        if (null == sisSchedule)
            throw new IncorrectParameterException("Schedule not found: " + ssId);
        SisLocation sisLocation = sisLocationMapper.selectByPrimaryKey(slId);
        if (null == sisLocation)
            throw new IncorrectParameterException("Location not found: " + slId);

        sisSchedule.setSlId(sisLocation.getSlId());
        boolean success = sisScheduleMapper.updateByPrimaryKey(sisSchedule) > 0;
        if (success)
            log.info("Success in update schedule location: ssId " + ssId + " " +
                "slId " + slId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        jsonObject.put("sisSchedule", new JSONObject(sisSchedule));
        return jsonObject;
    }

    public JSONObject getLocation(Integer slId) throws IncorrectParameterException {
        SisLocation sisLocation = sisLocationMapper.selectByPrimaryKey(slId);
        if (null == sisLocation)
            throw new IncorrectParameterException("Incorrecr location slId: " + slId);

        log.info("get location by id: " + slId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("sisLocation", new JSONObject(sisLocation));
        return jsonObject;
    }

    public JSONObject getLocations(Integer page, Integer pageSize,
                                   Integer slId, String slName) throws IncorrectParameterException {
        if (null == page) {
            throw new IncorrectParameterException("Incorrect page: " + null);
        }
        if (page < 1)
            throw new IncorrectParameterException("Incorrect page: " + page +
                " (must equal or bigger than 1)");
        if (null == pageSize)
            pageSize = 10;
        else if (pageSize <= 0 || pageSize > 500) {
            throw new IncorrectParameterException(
                "pageSize must between [1, 500]");
        }

        SisLocationExample sisLocationExample = new SisLocationExample();
        SisLocationExample.Criteria criteria =
            sisLocationExample.createCriteria();
        if (null != slId)
            criteria.andSlIdEqualTo(slId);
        if (null != slName)
            criteria.andSlNameLike(CourseService.getFuzzySearch(slName));

        PageHelper.startPage(page, pageSize);
        List<SisLocation> sisLocationList =
            sisLocationMapper.selectByExample(sisLocationExample);
        PageInfo<SisLocation> pageInfo = PageInfo.of(sisLocationList);

        if (sisLocationList.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("page", page);
            jsonObject.put("message", "No location");
            return jsonObject;
        }

        JSONObject pageJson = new JSONObject(pageInfo);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("page", page);
        jsonObject.put("data", pageJson);
        return jsonObject;
    }

    @Transactional
    public JSONObject createLocation(SisLocation sisLocation) {
        SisLocationExample sisLocationExample = new SisLocationExample();
        sisLocationExample.createCriteria().andSlNameLike("%" + sisLocation.getSlName() + "%");
        SisLocation stdSisLocation =
            sisLocationMapper.selectByExample(sisLocationExample)
                .stream()
                .findAny()
                .orElse(null);
        if (null != stdSisLocation) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message",
                "location exist: " + stdSisLocation.getSlId() + ", " + stdSisLocation.getSlName());
            return jsonObject;
        }

        boolean success = sisLocationMapper.insert(sisLocation) > 0;
        if (success)
            log.info("success insert location: " + sisLocation.getSlName());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        jsonObject.put("sisLocation", new JSONObject(sisLocation));
        return jsonObject;
    }

    @Transactional
    public JSONObject modifyLocation(Integer slId, SisLocation sisLocation) throws IncorrectParameterException {
        SisLocation stdSisLocation = sisLocationMapper.selectByPrimaryKey(slId);
        if (null == stdSisLocation)
            throw new IncorrectParameterException("Incorrect location: slid " + slId);

        stdSisLocation.setSlLat(sisLocation.getSlLat());
        stdSisLocation.setSlLong(sisLocation.getSlLong());
        boolean success =
            sisLocationMapper.updateByPrimaryKeySelective(stdSisLocation) > 0;
        if (success)
            log.info("Success in modifying location: " + slId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        jsonObject.put("sisLocation", new JSONObject(stdSisLocation));
        return jsonObject;
    }

    @Transactional
    public JSONObject deleteLocation(Integer slId) throws IncorrectParameterException {
        SisLocation sisLocation = sisLocationMapper.selectByPrimaryKey(slId);
        if (null == sisLocation)
            throw new IncorrectParameterException("Location not found: " + slId);
        boolean success = sisLocationMapper.deleteByPrimaryKey(slId) > 0;
        if (!success)
            log.info("Delete location error: " + slId);
        else
            log.info("Delete location success: " + slId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        return jsonObject;
    }

}
