package team.a9043.sign_in_system.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.mapper.SisCourseMapper;
import team.a9043.sign_in_system.mapper.SisDepartmentMapper;
import team.a9043.sign_in_system.mapper.SisLocationMapper;
import team.a9043.sign_in_system.mapper.SisUserMapper;
import team.a9043.sign_in_system.pojo.*;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * @author a9043
 */
@Service
public class ImportService {
    private Logger logger = LoggerFactory.getLogger(ImportService.class);
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private SisUserMapper sisUserMapper;
    @Resource
    private SisLocationMapper sisLocationMapper;
    @Resource
    private SisDepartmentMapper sisDepartmentMapper;
    @Resource
    private SisCourseMapper sisCourseMapper;

    @Transactional
    public void readCozInfo(File file) throws IOException,
        InvalidFormatException {
        final String[] rowNameList = {"课程序号", "课程名称", "教师工号", "授课教师", "学年度学期"
            , "上课地点", "上课时间", "年级", "上课院系", "实际人数", "容量"};

        List<List<?>> sheetList = readExcel(file);
        Map<String, Integer> cozMap = getMap(sheetList, rowNameList);
        if (null == cozMap) {
            logger.error("文件不合法");
            return;
        }

        //first
        addTeacher(sheetList, cozMap);
        addLocation(sheetList, cozMap);
        addDepartment(sheetList, cozMap);

        //second depend on first
        List<List<?>> newCozSheetList = addCourse(sheetList, cozMap);

        //third depend on second
        addJoinCourseTeaching(newCozSheetList, cozMap);
    }

    private Map<String, Integer> getMap(@Nonnull List<List<?>> sheetList,
                                        @Nonnull String[] rowNameList) {
        if (sheetList.size() <= 0)
            return null;
        List<?> row = sheetList.get(0);
        Map<String, Integer> rowNameMap = new HashMap<>();
        Arrays.stream(rowNameList)
            .forEach(rowName -> {
                int idx = row.indexOf(rowName);
                if (-1 == idx)
                    return;
                rowNameMap.put(rowName, idx);
            });
        if (rowNameMap.size() < rowNameList.length)
            return null;
        return rowNameMap;
    }

    @Transactional
    boolean addTeacher(List<List<?>> sheetList,
                       Map<String, Integer> cozMap) {
        String encryptPwd = bCryptPasswordEncoder.encode("123456");
        Set<SisUser> sisUserSet = sheetList.stream()
            .skip(1)
            .parallel()
            .map(row -> {
                String[] usrIds =
                    row.get(cozMap.get("教师工号")).toString().split(",");
                String[] usrNames =
                    row.get(cozMap.get("授课教师")).toString().split(",");

                if (usrIds.length != usrNames.length) {
                    logger.error(sheetList.indexOf(row) + " 教师信息错误");
                    return new ArrayList<SisUser>();
                }

                return IntStream.range(0, usrIds.length)
                    .mapToObj(i -> {
                        List<String> authList = new ArrayList<>();
                        authList.add("TEACHER");
                        SisUser sisUser = new SisUser();
                        sisUser.setSuId(usrIds[i].trim());
                        sisUser.setSuName(usrNames[i].trim());
                        sisUser.setSuAuthorities(authList);
                        sisUser.setSuPassword(encryptPwd);
                        return sisUser;
                    })
                    .collect(Collectors.toList());
            })
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

        List<String> suIdList =
            sisUserSet.parallelStream().map(SisUser::getSuId).collect(Collectors.toList());
        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria().andSuIdIn(suIdList);
        List<String> oldSuIdList =
            sisUserMapper.selectByExample(sisUserExample)
                .stream()
                .map(SisUser::getSuId)
                .collect(Collectors.toList());
        List<SisUser> insertSisUserList = sisUserSet.parallelStream()
            .filter(sisUser -> {
                String suId = sisUser.getSuId();
                return !oldSuIdList.contains(suId);
            })
            .collect(Collectors.toList());
        int res = sisUserMapper.insertList(insertSisUserList);
        if (res <= 0) {
            logger.error("error insert teachers");
            return false;
        } else {
            logger.info("success insert teachers: " + res);
            return true;
        }
    }

    @Transactional
    boolean addLocation(List<List<?>> sheetList,
                        Map<String, Integer> cozMap) {
        Set<String> firstLocStrSet = sheetList.stream().skip(1).parallel()
            .map(row -> row.get(cozMap.get("上课地点")).toString().split(","))
            .flatMap(Arrays::stream)
            .map(locStr -> locStr.trim().replaceAll("[a-zA-z0-9\\-、\\s]",
                ""))
            .map(locStr -> locStr.replaceAll("[樓]", "楼"))
            .filter(locStr -> !locStr.equals("") && locStr.length() > 1)
            .collect(Collectors.toSet());

        SisLocationExample sisLocationExample = new SisLocationExample();
        sisLocationExample.createCriteria().andSlNameIn(new ArrayList<>(firstLocStrSet));
        List<String> oldSlNameList =
            sisLocationMapper.selectByExample(sisLocationExample)
                .stream()
                .map(SisLocation::getSlName)
                .collect(Collectors.toList());

        List<SisLocation> sisLocationList = firstLocStrSet.parallelStream()
            .filter(s -> !oldSlNameList.contains(s))
            .map(s -> {
                SisLocation sisLocation = new SisLocation();
                sisLocation.setSlName(s);
                return sisLocation;
            })
            .collect(Collectors.toList());
        int res = sisLocationMapper.insertList(sisLocationList);
        if (res <= 0) {
            logger.error("error insert locations");
            return false;
        } else {
            logger.info("success insert locations: " + res);
            return true;
        }
    }

    @Transactional
    boolean addDepartment(List<List<?>> sheetList,
                          Map<String, Integer> cozMap) {
        Set<String> firstDepStrSet = sheetList.stream().skip(1)
            .map(row -> row.get(cozMap.get("上课院系")).toString().split(" "))
            .flatMap(Arrays::stream)
            .map(String::trim)
            .filter(depStr -> !depStr.startsWith("（") && !depStr.startsWith(
                "("))
            .filter(depStr -> !depStr.equals("") && depStr.length() > 1)
            .collect(Collectors.toSet());

        SisDepartmentExample sisDepartmentExample = new SisDepartmentExample();
        sisDepartmentExample.createCriteria().andSdNameIn(new ArrayList<>(firstDepStrSet));
        List<String> oldSdNameList =
            sisDepartmentMapper.selectByExample(sisDepartmentExample)
                .stream()
                .map(SisDepartment::getSdName)
                .collect(Collectors.toList());
        List<SisDepartment> sisDepartments = firstDepStrSet.parallelStream()
            .filter(s -> !oldSdNameList.contains(s))
            .map(s -> {
                SisDepartment sisDepartment = new SisDepartment();
                sisDepartment.setSdName(s);
                return sisDepartment;
            })
            .collect(Collectors.toList());
        int res = sisDepartmentMapper.insertList(sisDepartments);
        if (res <= 0) {
            logger.error("error insert departments");
            return false;
        } else {
            logger.info("success insert departments: " + res);
            return true;
        }
    }

    @Transactional
    List<List<?>> addCourse(List<List<?>> sheetList,
                            Map<String, Integer> cozMap) {
        List<SisCourse> sisCourseList = sheetList.stream().skip(1).parallel()
            .map(row -> {
                String scId = row.get(cozMap.get("课程序号")).toString().trim();
                if ("".equals(scId))
                    return null;
                String scName = row.get(cozMap.get("课程名称")).toString().trim();
                Integer scMaxSize = Double.valueOf(
                    row.get(cozMap.get("容量")).toString()).intValue();
                Integer scActSize = Double.valueOf(
                    row.get(cozMap.get("实际人数")).toString()).intValue();
                Integer scGrade = Optional
                    .of(row.get(cozMap.get("年级")).toString().trim())
                    .filter(gradeStr -> !gradeStr.equals(""))
                    .map(Integer::valueOf)
                    .orElse(null);
                SisCourse sisCourse = new SisCourse();
                sisCourse.setScId(scId);
                sisCourse.setScName(scName);
                sisCourse.setScMaxSize(scMaxSize);
                sisCourse.setScActSize(scActSize);
                sisCourse.setScGrade(scGrade);
                return sisCourse;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        List<String> suIdList =
            sisCourseList.parallelStream().map(SisCourse::getScId).collect(Collectors.toList());
        SisCourseExample sisCourseExample = new SisCourseExample();
        sisCourseExample.createCriteria().andScIdIn(suIdList);
        List<String> scIdList =
            sisCourseMapper.selectByExample(sisCourseExample).stream().map(SisCourse::getScId).collect(Collectors.toList());
        List<SisCourse> insertSisCourseList = sisCourseList.parallelStream()
            .filter(sisCourse -> !scIdList.contains(sisCourse.getScId()))
            .collect(Collectors.toList());
        int res = sisCourseMapper.insertList(insertSisCourseList);
        if (res <= 0) {
            logger.error("error insert courses");
            return null;
        } else {
            logger.info("success insert courses: " + res);
            List<String> insertScIdList =
                insertSisCourseList.parallelStream().map(SisCourse::getScId).collect(Collectors.toList());
            return sheetList.stream().skip(1).parallel()
                .filter(row -> {
                    String scId = row.get(cozMap.get("课程序号")).toString().trim();
                    if ("".equals(scId))
                        return false;
                    return insertScIdList.contains(scId);
                })
                .collect(Collectors.toList());
        }
    }

    @Transactional
    boolean addJoinCourseTeaching(List<List<?>> sheetList,
                                  Map<String, Integer> cozMap) {
        return false;
    }

    List<List<?>> readExcel(File file) throws IOException,
        InvalidFormatException {
        if (null == file)
            throw new IOException("File not found: null");
        if (!file.exists())
            throw new IOException("File not found: " + file.getAbsolutePath());
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);

        return StreamSupport.stream(sheet.spliterator(), true)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparingInt(Row::getRowNum))
            .map(row -> StreamSupport.stream(row.spliterator(), false)
                .map(cell -> {
                    if (null == cell || CellType.BLANK == cell.getCellTypeEnum())
                        return "";
                    switch (cell.getCellTypeEnum()) {
                        case STRING:
                            return cell.getStringCellValue();
                        case NUMERIC:
                            return cell.getNumericCellValue();
                        case BOOLEAN:
                            return cell.getBooleanCellValue();
                        case BLANK:
                            return "";
                        default:
                            return cell.toString();
                    }
                })
                .collect(Collectors.toList()))
            .collect(Collectors.toList());
    }
}
