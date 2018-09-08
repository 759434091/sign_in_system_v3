package team.a9043.sign_in_system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.pojo.SisLocation;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;
import team.a9043.sign_in_system.service.SignInService;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * @author a9043
 */
@RestController
public class SignInController {
    private SecretKeySpec secretKey;
    @Resource
    private SignInService signInService;

    public SignInController(@Value("${location.secretKey}")
                                String secretKeyStr) {
        this.secretKey =
            new SecretKeySpec(Base64.getDecoder().decode(secretKeyStr), "AES");
    }

    @PutMapping("/schedules/{ssId}/locations/{slId}")
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMINISTRATOR')")
    public JSONObject modifyScheduleLocation(@PathVariable Integer ssId,
                                             @PathVariable Integer slId) throws IncorrectParameterException {
        return signInService.modifyScheduleLocation(ssId, slId);
    }

    @GetMapping("/locations/{slId}")
    public JSONObject getLocation(@PathVariable Integer slId) throws IncorrectParameterException {
        return signInService.getLocation(slId);
    }

    @GetMapping("/locations")
    public JSONObject getLocations(@RequestParam Integer page,
                                   @RequestParam Integer pageSize,
                                   @RequestParam(required = false) Integer slId,
                                   @RequestParam(required = false) String slName) throws IncorrectParameterException {
        return signInService.getLocations(page, pageSize, slId, slName);
    }

    @PutMapping("/locations/{slId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject modifyLocation(@PathVariable Integer slId,
                                     @RequestBody SisLocation sisLocation) throws IncorrectParameterException {
        return signInService.modifyLocation(slId, sisLocation);
    }

    @DeleteMapping("/locations/{slId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject deleteLocation(@PathVariable Integer slId) throws IncorrectParameterException {
        return signInService.deleteLocation(slId);
    }

    @PostMapping("/locations")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject createLocation(@RequestBody SisLocation sisLocation) {
        return signInService.createLocation(sisLocation);
    }

    @GetMapping("/users/{suId}/signIns")
    public JSONObject getSignIns(@PathVariable String suId) {
        return signInService.getStuSignIns(suId);
    }

    @GetMapping("/courses/{scId}/signIns")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','STUDENT','TEACHER')")
    @ApiOperation("获得签到以及历史")
    public JSONObject getSignIns(@TokenUser @ApiIgnore SisUser sisUser,
                                 @PathVariable @ApiParam("课程") String scId,
                                 @RequestParam
                                 @ApiParam(value = "查询类型", allowableValues =
                                     "student, teacher, administrator")
                                     String queryType) throws IncorrectParameterException, InvalidPermissionException {
        switch (queryType) {
            case "teacher":
                if (!sisUser.getSuAuthoritiesStr().contains("TEACHER")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + queryType);
                }
                return signInService.getSignIns(scId);
            case "administrator":
                if (!sisUser.getSuAuthoritiesStr().contains("ADMINISTRATOR")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + queryType);
                }
                return signInService.getSignIns(scId);
            case "student":
                if (!sisUser.getSuAuthoritiesStr().contains("STUDENT")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + queryType);
                }
                return signInService.getSignIns(sisUser, scId);
            default:
                throw new IncorrectParameterException(
                    "Incorrect queryType: " + queryType);
        }
    }

    @PostMapping("/schedules/{ssId}/signIns")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','TEACHER')")
    @ApiOperation("发起签到")
    public JSONObject createSignIn(@TokenUser @ApiIgnore SisUser sisUser,
                                   @PathVariable @ApiParam("排课") Integer ssId) throws InvalidTimeParameterException, InvalidPermissionException {
        LocalDateTime localDateTime = LocalDateTime.now();

        return signInService.createSignIn(sisUser, ssId, localDateTime);
    }

    @GetMapping("/schedules/{ssId}/signIns/week/{week}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','TEACHER')")
    @ApiOperation("获得签到")
    public JSONObject getSignIn(@PathVariable @ApiParam("排课") Integer ssId,
                                @PathVariable @ApiParam("签到周") Integer week) {
        return signInService.getSignIn(ssId, week);
    }

    @PostMapping("/schedules/{ssId}/signIns/doSignIn")
    @PreAuthorize("hasAnyAuthority('STUDENT') && authentication.sisUser.type" +
        ".equals('code')")
    @ApiOperation("学生签到")
    public JSONObject signIn(@TokenUser @ApiIgnore SisUser sisUser,
                             @PathVariable @ApiParam("排课") Integer ssId,
                             @RequestHeader("Access-Token")
                             @ApiParam(value = "json的加密内容进行Base64编码",
                                 allowableValues = "{loc_lat: Double, " +
                                     "loc_long: Double}") String base64EncodeAESBytesStr) throws IncorrectParameterException, InvalidTimeParameterException, InvalidPermissionException {
        JSONObject locationJson;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            String locationString =
                new String(cipher.doFinal(Base64.getDecoder().decode(base64EncodeAESBytesStr)));
            locationJson =
                new JSONObject(locationString);
        } catch (JSONException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException | IllegalArgumentException e) {
            throw new IncorrectParameterException(e.getMessage() + " " +
                "Base64Str: " + base64EncodeAESBytesStr);
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            signInService.signIn(sisUser, ssId, localDateTime, locationJson));
        return jsonObject;
    }
}
