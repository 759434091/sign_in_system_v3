package team.a9043.sign_in_system.controller;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import team.a9043.sign_in_system.exception.UnauthorizedException;
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

    @RequestMapping(value = "/tokens/{code}",
        method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject getTokens(@PathVariable String code) throws WxServerException,
        UnauthorizedException {
        return userService.getTokensByCode(code);
    }
}
