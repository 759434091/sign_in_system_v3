package team.a9043.sign_in_system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.WxServerException;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;
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


    @ApiOperation(value = "初次修改微信绑定", notes = "根据code修改微信绑定")
    @PutMapping(value = "/users/{suId}")
    public JSONObject modifyBindUser(@TokenUser @ApiIgnore SisUser sisUser,
                                     @PathVariable @ApiParam("用户名") String suId,
                                     @RequestParam @ApiParam("微信code") String code) throws WxServerException, IncorrectParameterException {
        if (!sisUser.getSuId().equals(suId))
            throw new IncorrectParameterException("Incorrect suId: " + suId);
        return userService.modifyBindUser(sisUser, code);
    }

    @ApiOperation(value = "获取Token",
        notes = "用户名密码获取Token",
        httpMethod = "POST")
    @SuppressWarnings("unused")
    public void getTokens(@ApiParam("用户名") @RequestParam String suId,
                          @ApiParam("密码") @RequestParam String suPassword) {
    }
}
