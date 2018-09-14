package team.a9043.JavaUtil;

import team.a9043.pojo.Schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class TransSchedule {

    /**
     * 学期开始日期
     * 课程开始时间
     */
    public static final int MAX_WEEK = 19;
    private static final LocalDate START_DATE = returnStartDate();
    private static final LocalTime C1_TIME = LocalTime.of(8, 30);
    private static final LocalTime C2_TIME = LocalTime.of(9, 20);
    private static final LocalTime C3_TIME = LocalTime.of(10, 20);
    private static final LocalTime C4_TIME = LocalTime.of(11, 10);
    private static final LocalTime C5_TIME = LocalTime.of(14, 30);
    private static final LocalTime C6_TIME = LocalTime.of(15, 20);
    private static final LocalTime C7_TIME = LocalTime.of(16, 20);
    private static final LocalTime C8_TIME = LocalTime.of(17, 10);
    private static final LocalTime C9_TIME = LocalTime.of(19, 30);
    private static final LocalTime C10_TIME = LocalTime.of(20, 10);
    private static final LocalTime C11_TIME = LocalTime.of(21, 10);
    private static final LocalTime C12_TIME = LocalTime.of(22, 0);
    private static final LocalTime END_TIME = LocalTime.of(22, 50);

    private static LocalDate returnStartDate() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("startDate");
        return LocalDate.of(Integer.valueOf(resourceBundle.getString("year")),
                Integer.valueOf(resourceBundle.getString("month")),
                Integer.valueOf(resourceBundle.getString("day")));
    }

    /**
     * 转换LocalDateTime对象为Schedule 排课对象
     *
     * @param localDateTime 日期时间
     * @return 转换结果 Schedule
     */
    public static Schedule getSchedule(LocalDateTime localDateTime) {
        Schedule schedule = new Schedule();
        schedule.setSchYear(getYear(localDateTime));
        schedule.setSchWeek(getWeek(localDateTime));
        schedule.setSchDay(getDay(localDateTime));
        schedule.setSchFortnight(getFortnight(localDateTime));
        schedule.setSchTerm(getTerm(localDateTime));
        schedule.setSchTime(getTime(localDateTime));
        return schedule;
    }

    public static int getYear(LocalDateTime localDateTime) {
        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        if (month < 8) {
            return year - 1;
        } else {
            return year;
        }
    }

    /**
     * 获得第几周
     *
     * @param localDateTime 日期时间
     * @return 周数
     */
    public static int getWeek(LocalDateTime localDateTime) {
        long days = START_DATE.until(localDateTime, ChronoUnit.DAYS);
        return (int) (days / 7) + 1;
    }

    /**
     * 获得dayOfWeek
     *
     * @param localDateTime 日期时间
     * @return dayOfWeek
     */
    private static int getDay(LocalDateTime localDateTime) {
        return localDateTime.getDayOfWeek().getValue();
    }

    /**
     * 获得单双周
     *
     * @param localDateTime 日期时间
     * @return 1 = 单周, 2 = 双周
     */
    private static int getFortnight(LocalDateTime localDateTime) {
        return (getWeek(localDateTime) % 2 == 0) ? 2 : 1;
    }

    /**
     * 获得学期
     *
     * @param localDateTime 日期时间
     * @return false = 上学期, true =  下学期
     */
    private static boolean getTerm(LocalDateTime localDateTime) {
        int month = localDateTime.getMonthValue();
        return (month <= 8 && month >= 2);
    }

    /**
     * 获得第几节课
     *
     * @param localDateTime 日期时间
     * @return 课数
     */
    private static int getTime(LocalDateTime localDateTime) {
        LocalTime localTime = localDateTime.toLocalTime();
        if (localTime.isAfter(C1_TIME.minusMinutes(10)) && localTime.isBefore(C2_TIME)) {
            return 1;
        } else if (localTime.isAfter(C2_TIME.minusMinutes(10)) && localTime.isBefore(C3_TIME)) {
            return 2;
        } else if (localTime.isAfter(C3_TIME.minusMinutes(10)) && localTime.isBefore(C4_TIME)) {
            return 3;
        } else if (localTime.isAfter(C4_TIME.minusMinutes(10)) && localTime.isBefore(C5_TIME)) {
            return 4;
        } else if (localTime.isAfter(C5_TIME.minusMinutes(10)) && localTime.isBefore(C6_TIME)) {
            return 5;
        } else if (localTime.isAfter(C6_TIME.minusMinutes(10)) && localTime.isBefore(C7_TIME)) {
            return 6;
        } else if (localTime.isAfter(C7_TIME.minusMinutes(10)) && localTime.isBefore(C8_TIME)) {
            return 7;
        } else if (localTime.isAfter(C8_TIME.minusMinutes(10)) && localTime.isBefore(C9_TIME)) {
            return 8;
        } else if (localTime.isAfter(C9_TIME.minusMinutes(10)) && localTime.isBefore(C10_TIME)) {
            return 9;
        } else if (localTime.isAfter(C10_TIME.minusMinutes(10)) && localTime.isBefore(C11_TIME)) {
            return 10;
        } else if (localTime.isAfter(C11_TIME.minusMinutes(10)) && localTime.isBefore(C12_TIME)) {
            return 11;
        } else if (localTime.isAfter(C11_TIME.minusMinutes(10)) && localTime.isBefore(END_TIME)) {
            return 12;
        } else {
            return 0;
        }
    }

    /**
     * 获得课程开始时间
     *
     * @param time 第几节
     * @return localTime 课程开始时间
     */
    public static LocalTime getTime(int time) {
        switch (time) {
            case 1: {
                return C1_TIME;
            }
            case 2: {
                return C2_TIME;
            }
            case 3: {
                return C3_TIME;
            }
            case 4: {
                return C4_TIME;
            }
            case 5: {
                return C5_TIME;
            }
            case 6: {
                return C6_TIME;
            }
            case 7: {
                return C7_TIME;
            }
            case 8: {
                return C8_TIME;
            }
            case 9: {
                return C9_TIME;
            }
            case 10: {
                return C10_TIME;
            }
            case 11: {
                return C11_TIME;
            }
            case 12: {
                return C12_TIME;
            }
            default:
                return null;
        }
    }
}
