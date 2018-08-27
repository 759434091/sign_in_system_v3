package team.a9043.sign_in_system.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import team.a9043.sign_in_system.mapper.SisUserMapper;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.pojo.SisUserExample;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author a9043
 */
@Component
public class AsyncJoinService {
    @Resource
    private SisUserMapper sisUserMapper;

    @Async
    public Future<List<SisUser>> joinSisUser(List<String> suIdList) {
        if (suIdList.isEmpty()) {
            return new AsyncResult<>(new ArrayList<>());
        } else {
            SisUserExample sisUserExample = new SisUserExample();
            sisUserExample.createCriteria().andSuIdIn(suIdList);
            return new AsyncResult<>(sisUserMapper.selectByExample(sisUserExample));
        }
    }


}
