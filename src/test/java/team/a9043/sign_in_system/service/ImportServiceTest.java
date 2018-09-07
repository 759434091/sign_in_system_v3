package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import team.a9043.sign_in_system.exception.IncorrectParameterException;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ImportServiceTest {
    @Resource
    private ImportService importService;

    @Test
    public void readExcel() throws IOException, InvalidFormatException {
        File file = new File("/media/a9043/Sixth1/上线测试doc/教学任务列表.xls");
        List<List<?>> listList = importService.readExcel(new FileInputStream(file));
        log.info(new JSONArray(listList).toString(2));
    }

    @Test
    public void readCozInfo() throws IOException, InvalidFormatException {
        File file = new File("/media/a9043/Sixth1/上线测试doc/教学任务列表.xls");
        importService.readCozInfo("", new FileInputStream(file));
    }

    @Test
    public void readStuInfo() throws IOException, InvalidFormatException {
        File file = new File("/media/a9043/Sixth1/上线测试doc/上课名单导出-2.xls");
        importService.readStuInfo("", new FileInputStream(file));
    }

    @Test
    @Transactional
    public void deleteCourse() throws IncorrectParameterException {
        JSONObject jsonObject = importService.deleteCourse("A");
        log.info(jsonObject.toString(2));
    }
}
