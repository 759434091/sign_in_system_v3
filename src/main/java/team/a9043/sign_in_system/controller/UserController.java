package team.a9043.sign_in_system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import team.a9043.sign_in_system.exception.WxServerException;
import team.a9043.sign_in_system.service.UserService;

import javax.annotation.Resource;

/**
 * @author a9043
 */
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @ApiOperation(value = "获取token", notes = "根据code获取token")
    @RequestMapping(value = "/tokens/{code}",
        method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject getTokens(@PathVariable
                                @ApiParam(value = "微信获取code") String code) throws WxServerException {
        return userService.getTokensByCode(code);
    }
}
