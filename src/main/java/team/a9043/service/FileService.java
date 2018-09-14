package team.a9043.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.pojo.CozImport;
import team.a9043.pojo.StuAttImport;

import java.io.File;
import java.util.LinkedHashMap;

public interface FileService {

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    int[] insertStuAtt(StuAttImport stuAttImport);

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    boolean insertCozSch(CozImport cozImport);

    LinkedHashMap<String, Object> readCozExcel(File file);

    LinkedHashMap<String, Object> readStuExcel(File file);
}
