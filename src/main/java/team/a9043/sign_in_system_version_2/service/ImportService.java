package team.a9043.sign_in_system_version_2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system_version_2.mapper.FileMapper;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.Location;
import team.a9043.sign_in_system_version_2.pojo.Schedule;
import team.a9043.sign_in_system_version_2.pojo.User;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("importService")
public class ImportService {
    private final Logger logger = LoggerFactory.getLogger(ImportService.class);
    @Resource
    private FileMapper fileMapper;

    @Transactional
    public void addTeacher(ArrayList<ArrayList<Object>> result, HashMap<String, Integer> cozMap) {
        Set<User> firstTeacherSet = new HashSet<>();
        result.stream().skip(1).forEach(row -> {
            String[] usrIds = row.get(cozMap.get("教师工号")).toString().split(",");
            String[] usrNames = row.get(cozMap.get("授课教师")).toString().split(",");

            if (usrIds.length != usrNames.length) {
                logger.error(result.indexOf(row) + " 教师信息错误");
                return;
            }

            for (int i = 0; i < usrIds.length; i++) {
                User user = new User();
                user.setUsrId(usrIds[i].trim());
                user.setUsrName(usrNames[i].trim());
                firstTeacherSet.add(user);
            }
        });
        List<String> tchOldList = fileMapper.checkNewTeacher(firstTeacherSet);
        Set<User> teacherSet = firstTeacherSet.stream().filter(user -> !tchOldList.contains(user.getUsrId())).collect(Collectors.toSet());
        fileMapper.addTeacher(teacherSet);
    }

    @Transactional
    public void addLocation(ArrayList<ArrayList<Object>> result, HashMap<String, Integer> cozMap) {        // add location
        Set<String> firstLocStrSet = result.stream().skip(1)
                .map(row -> row.get(cozMap.get("上课地点")).toString().split(","))
                .flatMap(Arrays::stream)
                .map(locStr -> locStr.trim().replaceAll("[a-zA-z0-9\\-、\\s]", ""))
                .filter(locStr -> !locStr.equals("") && locStr.length() > 1)
                .collect(Collectors.toSet());
        List<String> locOldList = fileMapper.checkNewLocation(firstLocStrSet);
        Set<String> locStrSet = firstLocStrSet.stream().filter(locStr -> !locOldList.contains(locStr)).collect(Collectors.toSet());
        fileMapper.addLocation(locStrSet);
    }

    @Transactional
    public List<String> addCourse(ArrayList<ArrayList<Object>> result, HashMap<String, Integer> cozMap) {
        List<Course> firstCourseList = new ArrayList<>();
        result.stream().skip(1).forEach(row -> {
            String cozId = row.get(cozMap.get("课程序号")).toString().trim();
            String cozName = row.get(cozMap.get("课程名称")).toString().trim();
            Integer cozSize = Integer.valueOf(row.get(cozMap.get("容量")).toString().trim());
            Integer cozActSize = Integer.valueOf(row.get(cozMap.get("实际人数")).toString().trim());
            Integer cozGrade = Optional
                    .of(row.get(cozMap.get("年级")).toString().trim())
                    .filter(gradeStr -> !gradeStr.equals(""))
                    .map(Integer::valueOf)
                    .orElse(null);

            Course course = new Course();
            course.setCozId(cozId);
            course.setCozName(cozName);
            course.setCozSize(cozSize);
            course.setCozActSize(cozActSize);
            course.setCozGrade(cozGrade);
            firstCourseList.add(course);
        });
        List<String> cozOldList = fileMapper.checkNewCourse(firstCourseList);
        List<Course> courseList = firstCourseList.stream().filter(course -> !cozOldList.contains(course.getCozId())).collect(Collectors.toList());
        fileMapper.addCourse(courseList);
        return cozOldList;
    }

    @Transactional
    public void addCozTea(ArrayList<ArrayList<Object>> result, HashMap<String, Integer> cozMap, List<String> cozOldList){
        Set<HashMap<String, String>> firstCozTeaMapSet = new HashSet<>();
        result.stream().skip(1).forEach(row -> {
            String cozId = row.get(cozMap.get("课程序号")).toString().trim();
            // 已有记录不插入
            if (cozOldList.contains(cozId)) {
                return;
            }

            Arrays.stream(row.get(cozMap.get("教师工号")).toString().split(","))
                    .map(String::trim)
                    .filter(usrId -> !usrId.equals("") && usrId.length() > 1)
                    .forEach(usrId -> {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("cozId", cozId);
                        hashMap.put("usrId", usrId);
                        firstCozTeaMapSet.add(hashMap);
                    });
        });
        fileMapper.addCozTch(firstCozTeaMapSet);
    }

    @Transactional
    public void addDepartment(ArrayList<ArrayList<Object>> result, HashMap<String, Integer> cozMap) {
        Set<String> firstDepStrSet = result.stream().skip(1)
                .map(row -> row.get(cozMap.get("上课院系")).toString().split(" "))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(depStr -> !depStr.startsWith("（") && !depStr.startsWith("("))
                .filter(depStr -> !depStr.equals("") && depStr.length() > 1)
                .collect(Collectors.toSet());
        List<String> depStrList = fileMapper.checkNewDepartment(firstDepStrSet);
        Set<String> depStrSet = firstDepStrSet.stream().filter(depStr -> !depStrList.contains(depStr)).collect(Collectors.toSet());
        fileMapper.addDepartment(depStrSet);
    }

    @Transactional
    public void addCozDep(ArrayList<ArrayList<Object>> result, HashMap<String, Integer> cozMap, List<String> cozOldList) {
        Set<HashMap<String, String>> firstCozDepMapSet = new HashSet<>();
        result.stream().skip(1).forEach(row -> {
            String cozId = row.get(cozMap.get("课程序号")).toString().trim();
            // 已有记录不插入
            if (cozOldList.contains(cozId)) {
                return;
            }

            Arrays.stream(row.get(cozMap.get("上课院系")).toString().split(" "))
                    .map(String::trim)
                    .filter(depStr -> !depStr.startsWith("（") && !depStr.startsWith("("))
                    .filter(gradeStr -> !gradeStr.equals("") && gradeStr.length() > 1)
                    .forEach(depStr -> {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("cozId", cozId);
                        hashMap.put("depName", depStr);
                        firstCozDepMapSet.add(hashMap);
                    });
        });
        fileMapper.addCozDep(firstCozDepMapSet);
    }

    @Transactional
    public void addCozSch(ArrayList<ArrayList<Object>> result, HashMap<String, Integer> cozMap, List<String> cozOldList) {
        final HashMap<String, Byte> fortnightMap = new HashMap<String, Byte>() {
            private static final long serialVersionUID = 8866838960186252926L;

            {
                put("全", (byte) 0);
                put("单", (byte) 1);
                put("双", (byte) 2);
            }
        };
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
        final Pattern pattern = Pattern.compile("\\s*\\[(1[0-2]|[1-9])-(1[0-9]|[1-9])([单双])?]\\s*星期[一二三四五六日]\\s*(1[0-2]|[1-9])-(1[0-2]|[1-9])\\s*"); //上课时间匹配
        final Pattern patternSon1 = Pattern.compile("\\s*星期[一二三四五六日]\\s*"); //星期
        final Pattern patternSon2 = Pattern.compile("\\s*\\[(1[0-9]|[1-9])-(1[0-9]|[1-9])([单双])?]\\s*"); //周数
        final Pattern patternSon3 = Pattern.compile("\\s*(?<!\\[)(1[0-2]|[1-9])-(1[0-2]|[1-9])(?!])\\s*"); //节数
        List<Schedule> scheduleList = new ArrayList<>();
        result.stream().skip(1).forEach(row -> {
            String cozId = row.get(cozMap.get("课程序号")).toString().trim();
            // 已有记录不插入
            if (cozOldList.contains(cozId)) {
                return;
            }

            String[] matchStrList = row.get(cozMap.get("上课时间")).toString().split("\\s*,\\s*");
            String[] locmatchStrList = row.get(cozMap.get("上课地点")).toString().split("\\s*,\\s*");
            String[] yearAndTerm = row.get(cozMap.get("学年度学期")).toString().trim().split("-");
            int year = Integer.valueOf(yearAndTerm[0]);
            int termInt = Integer.valueOf(yearAndTerm[2]);
            boolean term = termInt != 1;

            // 检查信息
            if (matchStrList.length != locmatchStrList.length) {
                return;
            }

            for (int i = 0; i < matchStrList.length; i++) {
                //开始匹配
                Matcher matcher = pattern.matcher(matchStrList[i]);
                String locStr = locmatchStrList[i].trim().replaceAll("[a-zA-z0-9\\s]", "");
                if (matcher.find()) {
                    int day = 0;
                    int startWeek = 0;
                    int endWeek = 0;
                    byte fortnight = 0;
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
                        String weekStartStr = strings[0].substring(1);//获得开始周数
                        String weekEndStr = strings[strings.length - 1];//获得结束周数
                        if (weekEndStr.contains("单")) {
                            weekEndStr = weekEndStr.substring(0, weekEndStr.length() - 2);//取出 "]" 和 "周" 字符;
                            fortnight = fortnightMap.get("单");
                        } else if (weekEndStr.contains("双")) {
                            weekEndStr = weekEndStr.substring(0, weekEndStr.length() - 2);//取出 "]" 和 "周" 字符;
                            fortnight = fortnightMap.get("双");
                        } else {
                            weekEndStr = weekEndStr.substring(0, weekEndStr.length() - 1);//取出"]"字符;
                            fortnight = fortnightMap.get("全");
                        }
                        startWeek = Integer.valueOf(weekStartStr);
                        endWeek = Integer.valueOf(weekEndStr);
                    }
                    //获得节数
                    matcher = patternSon3.matcher(eStr);
                    if (matcher.find()) {
                        String[] strings = matcher.group().trim().split("-");//获得节数头尾
                        int startInt = Integer.valueOf(strings[0]);
                        int endInt = Integer.valueOf(strings[1]);
                        //插入排课
                        Schedule schedule = new Schedule();
                        Location location = new Location();
                        location.setLocName(locStr);
                        schedule.setLocation(location);
                        schedule.setSchStartTime(startInt);
                        schedule.setSchStartWeek(startWeek);
                        schedule.setSchDay(day);
                        schedule.setSchFortnight(fortnight);
                        schedule.setSchEndTime(endInt);
                        schedule.setSchEndWeek(endWeek);
                        schedule.setSchTerm(term);
                        schedule.setSchYear(year);
                        schedule.setCourseCozId(cozId);
                        scheduleList.add(schedule);
                    }
                }
            }


        });
        fileMapper.addSchedule(scheduleList);
    }

    @Transactional
    public Set<String> addStudent(ArrayList<ArrayList<Object>> result, HashMap<String, Integer> stuMap) {
        Set<HashMap<String, String>> firstStudentSet = new HashSet<>();
        Set<String> usrIdSet = new HashSet<>();

        // new student
        result.stream().skip(1).forEach(row -> {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("usrId", row.get(stuMap.get("学号")).toString().trim());
            hashMap.put("usrName", row.get(stuMap.get("姓名")).toString().trim());
            usrIdSet.add(hashMap.get("usrId"));
            firstStudentSet.add(hashMap);
        });
        List<String> oldStuList = fileMapper.checkNewStudent(usrIdSet);
        List<Map> studentList = firstStudentSet.stream().filter(map -> !oldStuList.contains(map.get("usrId"))).collect(Collectors.toList());
        fileMapper.addStudent(studentList);
        return usrIdSet;
    }

    @Transactional
    public void addAttendance(ArrayList<ArrayList<Object>> result, HashMap<String, Integer> stuMap, Set<String> usrIdSet) {
        // new Attendance
        List<HashMap<String, String>> firstAttList = new LinkedList<>();
        result.stream().skip(1).forEach(row -> {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("usrId", row.get(stuMap.get("学号")).toString().trim());
            hashMap.put("cozId", row.get(stuMap.get("课程序号")).toString().trim());
            usrIdSet.add(hashMap.get("usrId"));
            firstAttList.add(hashMap);
        });
        List<Map> oldAttList = fileMapper.checkNewAttendance(usrIdSet);
        List<Map> attList = firstAttList.stream().filter(map -> {
            String usrId = map.get("usrId");
            String cozId = map.get("cozId");

            for (Map anOldAttList : oldAttList) {
                if (anOldAttList.get("usrId").equals(usrId) &&
                        anOldAttList.get("cozId").equals(cozId)) {
                    return false;
                }
            }

            return true;
        }).collect(Collectors.toList());
        fileMapper.addAttendance(attList);
    }
}
