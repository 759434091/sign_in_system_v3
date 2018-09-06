package team.a9043.sign_in_system.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

/**
 * @author a9043
 */
@Service
public class FileService {
    @Resource
    private ImportService importService;

    public JSONObject readStuInfo(MultipartFile multipartFile) throws IOException, InvalidFormatException {
        String key =
            "SIS_Process_" + UUID.randomUUID().toString().replaceAll("-", "");
        importService.readStuInfo(key, multipartFile.getInputStream());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("key", key);
        return jsonObject;
    }

    public JSONObject readCozInfo(MultipartFile multipartFile) throws IOException, InvalidFormatException {
        String key =
            "SIS_Process_" + UUID.randomUUID().toString().replaceAll("-", "");
        importService.readCozInfo(key, multipartFile.getInputStream());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("key", key);
        return jsonObject;
    }
}
