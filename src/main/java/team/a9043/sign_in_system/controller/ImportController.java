package team.a9043.sign_in_system.controller;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.a9043.sign_in_system.service.ImportService;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author: 卢学能 zzz13129180808@gmail.com
 * @date: 2018/9/6 下午7:33
 */
@RestController
public class ImportController {
    @Resource
    private ImportService importService;

    @GetMapping("/progress/{key}")
    public JSONObject getProgress(@PathVariable String key) {
        return importService.getProgress(key);
    }

    @PostMapping("/courses/import")
    public JSONObject importCozInfo(@RequestPart MultipartFile multipartFile) throws IOException, InvalidFormatException {
        return importService.readCozInfo(multipartFile);
    }

    @PostMapping("/students/import")
    public JSONObject importStuInfo(@RequestPart MultipartFile multipartFile) throws IOException, InvalidFormatException {
        return importService.readStuInfo(multipartFile);
    }
}
