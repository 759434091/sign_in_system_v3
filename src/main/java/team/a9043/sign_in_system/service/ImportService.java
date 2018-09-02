package team.a9043.sign_in_system.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.mapper.*;
import team.a9043.sign_in_system.pojo.*;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;
    @Resource
    private SisJoinDepartMapper sisJoinDepartMapper;
    @Resource
    private SisScheduleMapper sisScheduleMapper;

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
        addCourseDepart(newCozSheetList, cozMap);
        addCourseSchedule(newCozSheetList, cozMap);
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
    public void readStuInfo(File file) throws IOException,
        InvalidFormatException {
        final String[] rowNameList = {"学号", "姓名", "课程序号"};

        List<List<?>> sheetList = readExcel(file);
        Map<String, Integer> stuMap = getMap(sheetList, rowNameList);
        if (null == stuMap) {
            logger.error("文件不合法");
            return;
        }

        //base
        addStudent(sheetList, stuMap);

        //depend on base
        addAttendance(sheetList, stuMap);
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
                        if ("".equals(usrIds[i].trim()))
                            return null;
                        List<String> authList = new ArrayList<>();
                        authList.add("TEACHER");
                        SisUser sisUser = new SisUser();
                        sisUser.setSuId(usrIds[i].trim());
                        sisUser.setSuName(usrNames[i].trim());
                        sisUser.setSuAuthorities(authList);
                        sisUser.setSuPassword(encryptPwd);
                        return sisUser;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            })
            .flatMap(Collection::parallelStream)
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
        List<SisDepartment> insertSisDepartmentList =
            firstDepStrSet.parallelStream()
                .filter(s -> !oldSdNameList.contains(s))
                .map(s -> {
                    SisDepartment sisDepartment = new SisDepartment();
                    sisDepartment.setSdName(s);
                    return sisDepartment;
                })
                .collect(Collectors.toList());
        int res = sisDepartmentMapper.insertList(insertSisDepartmentList);
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
        List<SisJoinCourse> sisJoinCourseList = sheetList.parallelStream()
            .map(row -> {
                String scId = row.get(cozMap.get("课程序号")).toString().trim();
                if ("".equals(scId))
                    return null;
                return Arrays.stream(row.get(cozMap.get("教师工号")).toString().split(","))
                    .map(String::trim)
                    .filter(s -> !s.equals("") && s.length() > 1)
                    .map(s -> {
                        SisJoinCourse sisJoinCourse = new SisJoinCourse();
                        sisJoinCourse.setJoinCourseType(SisJoinCourse.JoinCourseType.TEACHING.ordinal());
                        sisJoinCourse.setSuId(s);
                        sisJoinCourse.setScId(scId);
                        return sisJoinCourse;
                    })
                    .collect(Collectors.toList());
            })
            .filter(Objects::nonNull)
            .flatMap(Collection::parallelStream)
            .collect(Collectors.toList());
        int res = sisJoinCourseMapper.insertList(sisJoinCourseList);
        if (res <= 0) {
            logger.error("error insert sisJoinCourses");
            return false;
        } else {
            logger.info("success insert sisJoinCourses: " + res);
            return true;
        }
    }

    @Transactional
    boolean addCourseDepart(List<List<?>> sheetList,
                            Map<String, Integer> cozMap) {
        List<String> sdNameList = sheetList.parallelStream()
            .map(row -> Arrays.stream(row.get(cozMap.get(
                "上课院系")).toString().split(
                " "))
                .map(String::trim)
                .filter(depStr -> !depStr.startsWith("（") && !depStr.startsWith("("))
                .filter(gradeStr -> !gradeStr.equals("") && gradeStr.length() > 1))
            .flatMap(Stream::distinct)
            .collect(Collectors.toList());
        SisDepartmentExample sisDepartmentExample = new SisDepartmentExample();
        sisDepartmentExample.createCriteria().andSdNameIn(sdNameList);
        List<SisDepartment> sisDepartmentList =
            sisDepartmentMapper.selectByExample(sisDepartmentExample);

        List<SisJoinDepart> sisJoinDepartList = sheetList.parallelStream()
            .map(row -> {
                String scId = row.get(cozMap.get("课程序号")).toString().trim();
                if ("".equals(scId))
                    return null;
                return Arrays
                    .stream(
                        row.get(cozMap.get("上课院系")).toString().split(" "))
                    .map(String::trim)
                    .filter(depStr -> !depStr.startsWith("（") && !depStr.startsWith("("))
                    .filter(gradeStr -> !gradeStr.equals("") && gradeStr.length() > 1)
                    .map(s -> {
                        Integer sdId =
                            sisDepartmentList.parallelStream()
                                .filter(sisDepartment -> sisDepartment.getSdName().equals(s))
                                .findAny()
                                .map(SisDepartment::getSdId)
                                .orElse(null);
                        if (null == sdId)
                            return null;
                        SisJoinDepart sisJoinDepart = new SisJoinDepart();
                        sisJoinDepart.setScId(scId);
                        sisJoinDepart.setSdId(sdId);
                        return sisJoinDepart;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            })
            .filter(Objects::nonNull)
            .flatMap(Collection::parallelStream)
            .collect(Collectors.toList());

        int res = sisJoinDepartMapper.insertList(sisJoinDepartList);
        if (res <= 0) {
            logger.error("error insert sisJoinDeparts");
            return false;
        } else {
            logger.info("success insert sisJoinDeparts: " + res);
            return true;
        }
    }

    @Transactional
    boolean addCourseSchedule(List<List<?>> sheetList,
                              Map<String, Integer> cozMap) {
        final HashMap<String, Integer> dayMap = new HashMap<String, Integer>() {
            private static final long serialVersionUID = 331651763287947723L;

            {
                put("星期一", 1);
                put("星期二", 2);
                put("星期三", 3);
                put("星期四", 4);
                put("星期五", 5);
                put("星期六", 6);
                put("星期日", 7);
            }
        };
        final Pattern pattern = Pattern
            .compile("\\s*\\[(1[0-2]|[1-9])-(1[0-9]|[1-9])([单双])" +
                "?]\\s*星期[一二三四五六日]\\s*(1[0-2]|[1-9])-" +
                "(1[0-2]|[1-9])\\s*"); //上课时间匹配
        final Pattern patternSon1 = Pattern
            .compile("\\s*星期[一二三四五六日]\\s*"); //星期
        final Pattern patternSon2 = Pattern
            .compile("\\s*\\[(1[0-9]|[1-9])-(1[0-9]|[1-9])([单双])?]\\s*"); //周数
        final Pattern patternSon3 = Pattern
            .compile("\\s*(?<!\\[)(1[0-2]|[1-9])-(1[0-2]|[1-9])(?!])\\s*"); //节数

        //get location
        Set<String> slNameList = sheetList.parallelStream()
            .map(row -> row.get(cozMap.get("上课地点")).toString().split(","))
            .flatMap(Arrays::stream)
            .map(locStr -> locStr.trim().replaceAll("[a-zA-Z0-9\\-、\\s]",
                ""))
            .map(locStr -> locStr.replaceAll("[樓]", "楼"))
            .filter(locStr -> !locStr.equals("") && locStr.length() > 1)
            .collect(Collectors.toSet());
        SisLocationExample sisLocationExample = new SisLocationExample();
        sisLocationExample.createCriteria().andSlNameIn(new ArrayList<>(slNameList));
        List<SisLocation> locationList =
            sisLocationMapper.selectByExample(sisLocationExample);

        //set schedule
        List<SisSchedule> sisScheduleList = sheetList.parallelStream()
            .map(row -> {
                String scId = row.get(cozMap.get("课程序号")).toString().trim();
                if ("".equals(scId)) {
                    logger.error("scId");
                    return null;
                }

                String[] matchStrList =
                    row.get(cozMap.get("上课时间")).toString().split("\\s*,\\s*");
                String[] locMatchStrList =
                    row.get(cozMap.get("上课地点")).toString().split("\\s*,\\s*");
                String yearAndTerm =
                    row.get(cozMap.get("学年度学期")).toString().trim();
                if (matchStrList.length != locMatchStrList.length) {
                    logger.error("row err: 上课时间,上课地点 in " + scId);
                    return null;
                }

                return IntStream.range(0, matchStrList.length)
                    .parallel()
                    .mapToObj(i -> {
                        //获得地点
                        String slName = locMatchStrList[i]
                            .replaceAll("[a-zA-Z0-9\\-、\\s]", "")
                            .replaceAll("[樓]", "楼")
                            .trim();
                        if ("".equals(slName)) {
                            return null;
                        }
                        Integer slId = locationList.parallelStream()
                            .filter(sisLocation -> sisLocation.getSlName().equals(slName))
                            .findAny()
                            .map(SisLocation::getSlId)
                            .orElse(null);
                        if (null == slId) {
                            logger.error(String.format(
                                "row err: slId in %s, %d slId = %s",
                                scId, i, null));
                            return null;
                        }

                        //开始匹配
                        Matcher matcher;
                        matcher = pattern.matcher(matchStrList[i]);
                        if (!matcher.find())
                            return null;

                        String eStr = matcher.group();
                        //获得星期
                        int day;
                        matcher = patternSon1.matcher(eStr);
                        if (!matcher.find())
                            return null;

                        day = dayMap.get(matcher.group().trim());

                        //获得周数
                        SisSchedule.SsFortnight fortnight;
                        int startWeek;
                        int endWeek;
                        matcher = patternSon2.matcher(eStr);
                        if (!matcher.find())
                            return null;

                        String[] weekStrings =
                            matcher.group().trim().split("-");//获得周数头尾
                        String weekStartStr =
                            weekStrings[0].substring(1);//获得开始周数
                        String weekEndStr =
                            weekStrings[weekStrings.length - 1];//获得结束周数
                        if (weekEndStr.contains("单")) {
                            weekEndStr = weekEndStr.substring(0,
                                weekEndStr.length() - 2);//取出 "]" 和
                            // "周" 字符;
                            fortnight = SisSchedule.SsFortnight.ODD;
                        } else if (weekEndStr.contains("双")) {
                            weekEndStr = weekEndStr.substring(0,
                                weekEndStr.length() - 2);//取出 "]" 和
                            // "周" 字符;
                            fortnight = SisSchedule.SsFortnight.EVEN;
                        } else {
                            weekEndStr = weekEndStr.substring(0,
                                weekEndStr.length() - 1);//取出"]"字符;
                            fortnight = SisSchedule.SsFortnight.FULL;
                        }
                        startWeek = Integer.valueOf(weekStartStr);
                        endWeek = Integer.valueOf(weekEndStr);

                        //获得节数
                        matcher = patternSon3.matcher(eStr);
                        if (!matcher.find())
                            return null;

                        String[] timeStrings =
                            matcher.group().trim().split("-");//获得节数头尾
                        int startInt = Integer.valueOf(timeStrings[0]);
                        int endInt = Integer.valueOf(timeStrings[1]);
                        //插入排课
                        SisSchedule schedule = new SisSchedule();
                        schedule.setSsStartTime(startInt);
                        schedule.setSsStartWeek(startWeek);
                        schedule.setSsDayOfWeek(day);
                        schedule.setSsFortnight(fortnight.ordinal());
                        schedule.setSsEndTime(endInt);
                        schedule.setSsEndWeek(endWeek);
                        schedule.setSsYearEtTerm(yearAndTerm);
                        schedule.setScId(scId);
                        schedule.setSlId(slId);
                        return schedule;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            })
            .filter(Objects::nonNull)
            .flatMap(Collection::parallelStream)
            .collect(Collectors.toList());

        int res = sisScheduleMapper.insertList(sisScheduleList);
        if (res <= 0) {
            logger.error("error insert sisSchedules");
            return false;
        } else {
            logger.info("success insert sisSchedules: " + res);
            return true;
        }
    }

    @Transactional
    boolean addStudent(List<List<?>> sheetList,
                       Map<String, Integer> stuMap) {
        String encryptPwd = bCryptPasswordEncoder.encode("123456");
        // new student
        Set<SisUser> firstSisUserList = sheetList.stream().skip(1).parallel()
            .map(row -> {
                String suId = row.get(stuMap.get("学号")).toString().trim();
                if ("".equals(suId))
                    return null;
                List<String> authList = new ArrayList<>();
                authList.add("STUDENT");
                SisUser sisUser = new SisUser();
                sisUser.setSuId(suId);
                sisUser.setSuName(row.get(stuMap.get("姓名")).toString().trim());
                sisUser.setSuAuthorities(authList);
                sisUser.setSuPassword(encryptPwd);
                return sisUser;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        List<String> suIdList =
            firstSisUserList.parallelStream().map(SisUser::getSuId).collect(Collectors.toList());
        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria().andSuIdIn(suIdList);
        List<String> oldSuIdList =
            sisUserMapper.selectByExample(sisUserExample).stream().map(SisUser::getSuId).collect(Collectors.toList());

        List<SisUser> sisUserList = firstSisUserList.parallelStream()
            .filter(sisUser -> !oldSuIdList.contains(sisUser.getSuId()))
            .collect(Collectors.toList());

        int res = sisUserMapper.insertList(sisUserList);
        if (res <= 0) {
            logger.error("error insert students");
            return false;
        } else {
            logger.info("success insert students: " + res);
            return true;
        }
    }

    @Transactional
    boolean addAttendance(List<List<?>> sheetList,
                          Map<String, Integer> stuMap) {

        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();

        List<SisJoinCourse> sisJoinCourseList =
            sheetList.stream().skip(1).parallel()
                .map(row -> {
                    String scId = row.get(stuMap.get("课程序号")).toString().trim();
                    if ("".equals(scId))
                        return null;
                    String suId = row.get(stuMap.get("学号")).toString().trim();
                    if ("".equals(suId))
                        return null;
                    SisJoinCourse sisJoinCourse = new SisJoinCourse();
                    sisJoinCourse.setScId(scId);
                    sisJoinCourse.setSuId(suId);
                    sisJoinCourse.setJoinCourseType(SisJoinCourse.JoinCourseType.ATTENDANCE.ordinal());
                    sisJoinCourseExample.or()
                        .andScIdEqualTo(scId)
                        .andSuIdEqualTo(suId);
                    return sisJoinCourse;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        sisJoinCourseExample.getOredCriteria().removeIf(Objects::isNull);
        List<SisJoinCourse> oldSisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);

        List<SisJoinCourse> insertSisJoinCourse =
            sisJoinCourseList.parallelStream()
                .filter(sisJoinCourse -> !oldSisJoinCourseList.contains(sisJoinCourse))
                .collect(Collectors.toList());

        int res = sisJoinCourseMapper.insertList(insertSisJoinCourse);
        if (res <= 0) {
            logger.error("error insert student sisJoinCourses");
            return false;
        } else {
            logger.info("success insert student sisJoinCourses: " + res);
            return true;
        }
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
