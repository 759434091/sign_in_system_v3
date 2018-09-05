package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
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
        List<List<?>> listList = importService.readExcel(file);
        log.info(new JSONArray(listList).toString(2));
    }

    @Test
    public void readCozInfo() throws IOException, InvalidFormatException {
        File file = new File("/media/a9043/Sixth1/上线测试doc/教学任务列表.xls");
        importService.readCozInfo(file);
    }

    @Test
    public void readStuInfo() throws IOException, InvalidFormatException {
        File file = new File("/media/a9043/Sixth1/上线测试doc/上课名单导出-2.xls");
        importService.readStuInfo(file);
    }
}
