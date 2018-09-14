package team.a9043.sign_in_system.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.pojo.SisContact;
import team.a9043.sign_in_system.service.ContactService;

import javax.annotation.Resource;

/**
 * @author a9043
 */
@RestController
public class ContactController {
    @Resource
    public ContactService contactService;

    @PostMapping("/contacts")
    public JSONObject contact(@RequestBody @Validated SisContact sisContact,
                           BindingResult bindingResult) throws IncorrectParameterException {
        if (bindingResult.hasErrors()) {
            throw new IncorrectParameterException(new JSONArray(bindingResult.getAllErrors()).toString());
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            contactService.receiveKssContact(sisContact));
        return jsonObject;
    }
}
