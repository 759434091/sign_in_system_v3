package team.a9043.serviceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.mapper.FileMapper;
import team.a9043.pojo.*;
import team.a9043.service.FileService;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger infoLog = LogManager.getLogger(FileServiceImpl.class);
    private final FileMapper fileMapper;

    private final HashMap<String, Boolean> termMap = new HashMap<String, Boolean>() {
        private static final long serialVersionUID = 5237700157647378523L;

        {
            put("上", false);
            put("下", true);
        }
    }; //学期上下map
    private final HashMap<String, Integer> fortnightMap = new HashMap<String, Integer>() {
        private static final long serialVersionUID = 8866838960186252926L;

        {
            put("全", 0);
            put("单", 1);
            put("双", 2);
        }
    }; //单双周map
    private final HashMap<String, Integer> dayMap = new HashMap<String, Integer>() {
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
    }; //星期map
    private final Pattern pattern = Pattern.compile("\\s*\\[(1[0-2]|[1-9])-(1[0-9]|[1-9])(单|双)?]\\s*星期[一二三四五六日]\\s*(1[0-2]|[1-9])-(1[0-2]|[1-9])\\s*"); //上课时间匹配
    private final Pattern patternSon1 = Pattern.compile("\\s*星期[一二三四五六日]\\s*"); //星期
    private final Pattern patternSon2 = Pattern.compile("\\s*\\[(1[0-9]|[1-9])-(1[0-9]|[1-9])(单|双)?]\\s*"); //周数
    private final Pattern patternSon3 = Pattern.compile("\\s*(?<!\\[)(1[0-2]|[1-9])-(1[0-2]|[1-9])(?!])\\s*"); //节数

    //默认单元格内容为数字时格式
    private static DecimalFormat df = new DecimalFormat("0");
    // 默认单元格格式化日期字符串
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 格式化数字
    private static DecimalFormat nf = new DecimalFormat("0.000");

    private HashMap<String, Integer> cozMap = new HashMap<>();

    private HashMap<String, Integer> stuMap = new HashMap<>();

    @Autowired
    public FileServiceImpl(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    /**
     * 人工插入
     *
     * @param stuAttImport 学生名单
     * @return 插入结果
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public int[] insertStuAtt(StuAttImport stuAttImport) {
        stuMap.put("学号", 0);
        stuMap.put("姓名", 1);
        stuMap.put("课程序号", 2);
        ArrayList<Object> row0 = new ArrayList<Object>() {
            private static final long serialVersionUID = -5435840101877608661L;

            {
                this.add(0, "学号");
                this.add(1, "姓名");
                this.add(2, "课程序号");
            }
        };
        ArrayList<Object> row1 = new ArrayList<Object>() {
            private static final long serialVersionUID = -5435840101877608661L;

            {
                this.add(0, stuAttImport.getUserId());
                this.add(1, stuAttImport.getUserName());
                this.add(2, stuAttImport.getCozId());
            }
        };
        ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>() {
            private static final long serialVersionUID = -5435840101877608661L;

            {
                this.add(0, row0);
                this.add(1, row1);
            }
        };
        return addStuNewData(rowList);
    }

    /**
     * 人工插入 课程名单
     *
     * @param cozImport 课程名单
     * @return 插入结果
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean insertCozSch(CozImport cozImport) {
        boolean result = false;
        if (fileMapper.checkTchExist(cozImport.getTchId()) == null) {
            User teacher = new User(cozImport.getTchId(), null, null, cozImport.getTchName(), null);
            fileMapper.addTeacher(teacher);
        }
        if (fileMapper.checkCozExist(cozImport.getCozId()) == null) {
            if (fileMapper.checkTchExist(cozImport.getTchId()) != null) {
                Course course = new Course(cozImport.getCozId(), cozImport.getCozName(), new User(cozImport.getTchId(), null, null, null, null), null);
                result = fileMapper.addCourse((AdmCourse) course) > 0;
            }
        }
        if (fileMapper.checkSchExist(cozImport.getCozId()).size() == 0) {
            Matcher matcher = pattern.matcher(cozImport.getCozTime());
            if (matcher.find()) {
                int day = 0;
                int week = 0;
                String eStr = matcher.group();
                //获得星期
                matcher = patternSon1.matcher(eStr);
                if (matcher.find()) {
                    day = dayMap.get(matcher.group());
                }
                //获得周数
                matcher = patternSon3.matcher(eStr);
                if (matcher.find()) {
                    String[] strings = matcher.group().split("-");//获得周数头尾
                    String weekStr = strings[strings.length - 1];//获得结束周数
                    weekStr = weekStr.substring(0, weekStr.length() - 1);//取出"]"字符;
                    week = Integer.valueOf(weekStr);
                }
                //获得节数
                matcher = patternSon2.matcher(eStr);
                if (matcher.find()) {
                    String eInnerStr = matcher.group();
                    eInnerStr = eInnerStr.substring(1, eInnerStr.length() - 1);
                    String[] strings = eInnerStr.split("-");//获得节数头尾
                    int startInt = Integer.valueOf(strings[0]);
                    int endInt = Integer.valueOf(strings[1]);
                    //插入排课
                    result = true;
                    for (int j = startInt; j <= endInt; j++) {
                        Schedule schedule = new Schedule(
                                cozImport.getCozId(),
                                j,
                                day,
                                week,
                                cozImport.getCozYear(),
                                cozImport.getCozFortnight(),
                                0,
                                cozImport.isCozTerm(),
                                new Location(cozImport.getLocId(), 0, 0, 0, 0, 0, null),
                                null
                        );
                        result = result && fileMapper.insertSchedule(schedule) > 0;
                    }
                }
            }
        } else {
            result = false;
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public LinkedHashMap<String, Object> readCozExcel(File file) {
        final String[] rowNameList = {"课程序号", "课程名称", "教师工号", "授课教师", "学年度学期", "上课地点", "上课时间", "年级", "上课院系", "实际人数", "容量"};
        ArrayList<ArrayList<Object>> result;
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        int schNum;
        int totalRow;
        int[] checkRes;
        if (file == null) {
            return null;
        }
        result = readExcel(file);
        totalRow = result != null ? (result.size() > 1 ? result.size() - 1 : 0) : 0;
        if (checkValid(result, rowNameList, cozMap)) {
            checkRes = checkTchNewData(result);
            resultMap.put("TchNum", checkRes[0]);
            resultMap.put("AddedTchNum", checkRes[1]);
            resultMap.put("CozNum", checkRes[2]);
            resultMap.put("AddedCozMum", checkRes[3]);
            resultMap.put("SchNum", totalRow);
            if (checkRes[0] == checkRes[1] && checkRes[2] == checkRes[3]) {
                schNum = insertSchedule(result);
                resultMap.put("AddedSchMum", schNum);
            } else {
                resultMap.put("AddedSchMum", 0);
            }
        } else {
            resultMap.put("TchNum", 0);
            resultMap.put("AddedTchNum", 0);
            resultMap.put("CozNum", 0);
            resultMap.put("AddedCozMum", 0);
            resultMap.put("SchNum", 0);
            resultMap.put("AddedSchMum", 0);
        }
        return resultMap;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public LinkedHashMap<String, Object> readStuExcel(File file) {
        final String[] rowNameList = {"学号", "姓名", "课程序号"};
        ArrayList<ArrayList<Object>> result;
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        if (file == null) {
            return null;
        }
        result = readExcel(file);
        if (checkValid(result, rowNameList, stuMap)) {
            int[] resultArray = addStuNewData(result);
            resultMap.put("rowNum", resultArray[0]);
            resultMap.put("addedStuNum", resultArray[1]);
            resultMap.put("addedAttendNum", resultArray[2]);
        } else {
            resultMap.put("rowNum", 0);
            resultMap.put("addedStuNum", 0);
            resultMap.put("addedAttendNum", 0);
        }
        return resultMap;
    }

    private int[] checkTchNewData(ArrayList<ArrayList<Object>> result) {
        int newTch = 0;
        int addedTch = 0;
        int newCoz = 0;
        int addedCoz = 0;

        for (int i = 1; i < result.size(); i++) {
            ArrayList<Object> row = result.get(i);
            String userIdList[] = row.get(cozMap.get("教师工号")).toString().split(",");
            String userNameList[] = row.get(cozMap.get("授课教师")).toString().split(",");
            if (userIdList.length != userNameList.length) {
                infoLog.info("教职工信息错误在 " + i + "行");
            } else {
                for (int j = 0; j < userIdList.length; j++) {
                    String userId = userIdList[j];
                    String userName = userNameList[j];
                    if (!userId.equals("") && !userName.equals("") && fileMapper.checkTchExist(userId) == null) {
                        newTch++;
                        User teacher = new User(userId, null, null, userName, null);
                        addedTch += fileMapper.addTeacher(teacher);
                    }
                }
            }
            String cozId = row.get(cozMap.get("课程序号")).toString();
            String cozName = row.get(cozMap.get("课程名称")).toString();
            String cozDepart = row.get(cozMap.get("上课院系")).toString();
            Integer cozSize = Integer.valueOf(row.get(cozMap.get("容量")).toString());
            Integer cozActSize = Integer.valueOf(row.get(cozMap.get("实际人数")).toString());
            String cozGradeStr = row.get(cozMap.get("年级")).toString();
            Integer cozGrade = cozGradeStr.equals("") ? null : Integer.valueOf(cozGradeStr);
            if (fileMapper.checkCozExist(cozId) == null) {
                newCoz++;
                AdmCourse course = new AdmCourse();
                if (!userIdList[0].equals("") && !userNameList[0].equals("") && fileMapper.checkTchExist(userIdList[0]) != null) {
                    course.setCozId(cozId);
                    course.setCozName(cozName);
                    course.setTeacher(new User(userIdList[0], null, null, null, null));
                    course.setCozDepart(cozDepart.equals("") ? null : cozDepart);
                    course.setCozActSize(cozActSize);
                    course.setCozSize(cozSize);
                    course.setCozGrade(cozGrade);
                } else {
                    course.setCozId(cozId);
                    course.setCozName(cozName);
                    course.setCozDepart(cozDepart.equals("") ? null : cozDepart);
                    course.setCozActSize(cozActSize);
                    course.setCozSize(cozSize);
                    course.setCozGrade(cozGrade);
                }
                addedCoz += fileMapper.addCourse(course);
            }
        }
        return new int[]{newTch, addedTch, newCoz, addedCoz};
    }

    private int[] addStuNewData(ArrayList<ArrayList<Object>> result) {
        int rowNum = result.size() - 1;
        int addedStuNum = 0;
        int addedAttendNum = 0;
        for (int i = 1; i < result.size(); i++) {
            ArrayList<Object> row = result.get(i);
            String userId = row.get(stuMap.get("学号")).toString();
            String userName = row.get(stuMap.get("姓名")).toString();
            String cozId = row.get(stuMap.get("课程序号")).toString();
            if (fileMapper.checkStudent(userId) == null) {
                addedStuNum += fileMapper.addStudent(new User(userId, null, null, userName, null));
            }
            addedAttendNum += fileMapper.insertAttend(new Attend(cozId, userId));
        }
        return new int[]{rowNum, addedStuNum, addedAttendNum};
    }

    private boolean checkValid(ArrayList<ArrayList<Object>> result, String[] rowNameList, HashMap<String, Integer> map) {
        if (result != null && result.size() > 1) {
            ArrayList<Object> row = result.get(0);
            for (String aRowNameList : rowNameList) {
                int mapIndex;
                mapIndex = row.indexOf(aRowNameList);
                if (mapIndex >= 0) {
                    map.put(aRowNameList, mapIndex);
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private int insertSchedule(ArrayList<ArrayList<Object>> result) {
        int addNum = 0;
        for (int i = 1; i < result.size(); i++) {
            ArrayList<Object> row = result.get(i);
            String[] matchStrList = row.get(cozMap.get("上课时间")).toString().split("\\s*,\\s*");
            String[] locmatchStrList = row.get(cozMap.get("上课地点")).toString().split("\\s*,\\s*");
            String cozId = row.get(cozMap.get("课程序号")).toString().trim();
            String[] yearAndTerm = row.get(cozMap.get("学年度学期")).toString().trim().split("-");
            int year = Integer.valueOf(yearAndTerm[0]);
            int termInt = Integer.valueOf(yearAndTerm[2]);
            boolean term;
            term = termInt != 1; //判断学期上下
            //检查是否已有排课记录 && 检查上课信息和上课地点的匹配
            if (fileMapper.checkSchExist(cozId).size() == 0 && matchStrList.length == locmatchStrList.length) {
                //上课时间循环
                for (int j = 0; j < matchStrList.length; j++) {
                    //开始匹配
                    Matcher matcher = pattern.matcher(matchStrList[j]);
                    String locStr = locmatchStrList[j].trim();
                    Pattern locPattern = Pattern.compile("[\\u4E00-\\u9FA5]+");
                    Matcher locMatcher = locPattern.matcher(locStr);
                    Location location;
                    if (locMatcher.find()) {
                        locStr = locMatcher.group();
                        location = fileMapper.findLocation(locStr);
                    } else {
                        location = null;
                    }
                    if (matcher.find()) {
                        int day = 0;
                        int week = 0;
                        int fortnight = 0;
                        String eStr = matcher.group();
                        //获得星期
                        matcher = patternSon1.matcher(eStr);
                        if (matcher.find()) {
                            day = dayMap.get(matcher.group().trim());
                        }
                        //获得周数
                        matcher = patternSon2.matcher(eStr);
                        if (matcher.find()) {
                            String[] strings = matcher.group().trim().split("-");//获得周数头尾
                            String weekStr = strings[strings.length - 1];//获得结束周数
                            if (weekStr.contains("单")) {
                                weekStr = weekStr.substring(0, weekStr.length() - 2);//取出 "]" 和 "周" 字符;
                                fortnight = fortnightMap.get("单");
                            } else if (weekStr.contains("双")) {
                                weekStr = weekStr.substring(0, weekStr.length() - 2);//取出 "]" 和 "周" 字符;
                                fortnight = fortnightMap.get("双");
                            } else {
                                weekStr = weekStr.substring(0, weekStr.length() - 1);//取出"]"字符;
                                fortnight = fortnightMap.get("全");
                            }
                            week = Integer.valueOf(weekStr);
                        }
                        //获得节数
                        matcher = patternSon3.matcher(eStr);
                        if (matcher.find()) {
                            String[] strings = matcher.group().trim().split("-");//获得节数头尾
                            int startInt = Integer.valueOf(strings[0]);
                            int endInt = Integer.valueOf(strings[1]);
                            //插入排课
                            for (int k = startInt; k <= endInt; k++) {
                                Schedule schedule = new Schedule(
                                        cozId,
                                        k,
                                        day,
                                        week,
                                        year,
                                        fortnight,
                                        0,
                                        term,
                                        location,
                                        null
                                );
                                addNum += fileMapper.insertSchedule(schedule);
                            }
                        }
                    }
                }
            }
        }
        return addNum;
    }

    private ArrayList<ArrayList<Object>> readExcel(File file) {
        try {
            ArrayList<ArrayList<Object>> rowList = new ArrayList<>();
            ArrayList<Object> colList;
            Workbook workbook = WorkbookFactory.create(new FileInputStream(file));
            Sheet sheet = workbook.getSheetAt(0);
            Row row;
            Cell cell;
            Object value;
            for (int i = sheet.getFirstRowNum(), rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                colList = new ArrayList<>();
                if (row == null) {
                    //当读取行为空时
                    if (i != sheet.getPhysicalNumberOfRows()) {
                        //判断是否是最后一行
                        rowList.add(colList);
                    }
                    continue;
                } else {
                    rowCount++;
                }
                for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
                        //当该单元格为空
                        if (j != row.getLastCellNum()) {//判断是否是该行中最后一个单元格
                            colList.add("");
                        }
                        continue;
                    }
                    switch (cell.getCellTypeEnum()) {
                        case STRING:
                            value = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            if ("@".equals(cell.getCellStyle().getDataFormatString()) ||
                                    (Double.parseDouble(Math.round(cell.getNumericCellValue()) + ".0") == cell.getNumericCellValue())) {
                                value = df.format(cell.getNumericCellValue());
                            } else if ("General".equals(cell.getCellStyle()
                                    .getDataFormatString())) {
                                value = nf.format(cell.getNumericCellValue());
                            } else {
                                value = sdf.format(HSSFDateUtil.getJavaDate(cell
                                        .getNumericCellValue()));
                            }
                            break;
                        case BOOLEAN:
                            value = cell.getBooleanCellValue();
                            break;
                        case BLANK:
                            value = "";
                            break;
                        default:
                            value = cell.toString();
                    }
                    colList.add(value);
                }
                rowList.add(colList);
            }
            return rowList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
