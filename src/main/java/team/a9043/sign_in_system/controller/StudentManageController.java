package team.a9043.sign_in_system.controller;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.service.StudentManageService;

import javax.annotation.Resource;

@RestController
public class StudentManageController {
    @Resource
    private StudentManageService studentManageService;

    @GetMapping("/students")
    public JSONObject getStudents(@RequestParam Integer page,
                                  @RequestParam Integer pageSize,
                                  @RequestParam(required = false) String suId,
                                  @RequestParam(required = false) String suName) throws IncorrectParameterException {
        return studentManageService.getStudents(page, pageSize, suId, suName);
    }
}
