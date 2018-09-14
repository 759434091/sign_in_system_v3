package team.a9043.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.a9043.pojo.CozImport;
import team.a9043.pojo.StuAttImport;
import team.a9043.service.FileService;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

@Controller("fileController")
@SessionAttributes({"user"})
@RequestMapping("/File")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping("/uploadCozInfo")
    @ResponseBody
    public String uploadCozInfo(MultipartFile cozInfo) {
        if (cozInfo != null) {
            String fileName = cozInfo.getOriginalFilename();
            File dir = new File(System.getProperty("user.dir") + "/../tempImport");
            File file = new File(dir.getAbsolutePath() + File.separator + fileName);
            try {
                if (!file.getParentFile().exists()) {
                    boolean res = file.getParentFile().mkdirs();
                    if (!res) {
                        return null;
                    }
                }
                for (int i = 1; file.exists(); i++) {
                    file = new File(dir.getAbsolutePath() + File.separator + i + "_temp_" + fileName);
                }
                cozInfo.transferTo(file);
                LinkedHashMap<String, Object> resultMap = fileService.readCozExcel(file);
                StringBuilder resultString = new StringBuilder();
                for (String key : resultMap.keySet()) {
                    resultString.append("\n").append(key).append(": ").append(resultMap.get(key));
                }
                return "Status: Success" +
                        "\nFileName: " + fileName +
                        resultString.toString();
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @RequestMapping("/uploadStuInfo")
    @ResponseBody
    public String uploadStuInfo(MultipartFile stuInfo) {
        if (stuInfo != null) {
            String fileName = stuInfo.getOriginalFilename();
            File dir = new File(System.getProperty("user.dir") + "/../tempImport");
            File file = new File(dir.getAbsolutePath() + File.pathSeparator + fileName);
            try {
                if (!file.getParentFile().exists()) {
                    boolean res = file.getParentFile().mkdirs();
                    if (!res) {
                        return null;
                    }
                }
                for (int i = 1; file.exists(); i++) {
                    file = new File(dir.getAbsolutePath() + File.pathSeparator + i + "_temp_" + fileName);
                }
                stuInfo.transferTo(file);
                LinkedHashMap<String, Object> resultMap = fileService.readStuExcel(file);
                StringBuilder resultString = new StringBuilder();
                for (String key : resultMap.keySet()) {
                    resultString.append("\n").append(key).append(": ").append(resultMap.get(key));
                }
                return "Status: Success" +
                        "\nFileName: " + fileName +
                        resultString.toString();
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @RequestMapping("/insertCozSch")
    @ResponseBody
    public boolean insertCozSch(@RequestBody CozImport cozImport) {
        return fileService.insertCozSch(cozImport);
    }

    @RequestMapping("/insertStuAtt")
    @ResponseBody
    public int[] insertStuAtt(@RequestBody StuAttImport stuAttImport) {
        return fileService.insertStuAtt(stuAttImport);
    }
}




