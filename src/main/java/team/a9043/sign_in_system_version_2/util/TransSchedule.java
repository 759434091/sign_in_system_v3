package team.a9043.sign_in_system_version_2.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team.a9043.sign_in_system_version_2.pojo.Schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Component
public class TransSchedule {

    /**
     * 学期开始日期
     * 课程开始时间
     */
    public final int MAX_WEEK = 19;
    private final LocalDate START_DATE;
    private final LocalTime C1_TIME;
    private final LocalTime C2_TIME;
    private final LocalTime C3_TIME;
    private final LocalTime C4_TIME;
    private final LocalTime C5_TIME;
    private final LocalTime C6_TIME;
    private final LocalTime C7_TIME;
    private final LocalTime C8_TIME;
    private final LocalTime C9_TIME;
    private final LocalTime C10_TIME;
    private final LocalTime C11_TIME;
    private final LocalTime C12_TIME;
    private final LocalTime END_TIME;

    public TransSchedule(@Value("${schedule-setting.term-start-date.year}") int year,
                         @Value("${schedule-setting.term-start-date.month}") int month,
                         @Value("${schedule-setting.term-start-date.day}") int day,
                         @Value("${schedule-setting.class-1.hour}") int c1Hour,
                         @Value("${schedule-setting.class-1.minute}") int c1Minute,
                         @Value("${schedule-setting.class-2.hour}") int c2Hour,
                         @Value("${schedule-setting.class-2.minute}") int c2Minute,
                         @Value("${schedule-setting.class-3.hour}") int c3Hour,
                         @Value("${schedule-setting.class-3.minute}") int c3Minute,
                         @Value("${schedule-setting.class-4.hour}") int c4Hour,
                         @Value("${schedule-setting.class-4.minute}") int c4Minute,
                         @Value("${schedule-setting.class-5.hour}") int c5Hour,
                         @Value("${schedule-setting.class-5.minute}") int c5Minute,
                         @Value("${schedule-setting.class-6.hour}") int c6Hour,
                         @Value("${schedule-setting.class-6.minute}") int c6Minute,
                         @Value("${schedule-setting.class-7.hour}") int c7Hour,
                         @Value("${schedule-setting.class-7.minute}") int c7Minute,
                         @Value("${schedule-setting.class-8.hour}") int c8Hour,
                         @Value("${schedule-setting.class-8.minute}") int c8Minute,
                         @Value("${schedule-setting.class-9.hour}") int c9Hour,
                         @Value("${schedule-setting.class-9.minute}") int c9Minute,
                         @Value("${schedule-setting.class-10.hour}") int c10Hour,
                         @Value("${schedule-setting.class-10.minute}") int c10Minute,
                         @Value("${schedule-setting.class-11.hour}") int c11Hour,
                         @Value("${schedule-setting.class-11.minute}") int c11Minute,
                         @Value("${schedule-setting.class-12.hour}") int c12Hour,
                         @Value("${schedule-setting.class-12.minute}") int c12Minute,
                         @Value("${schedule-setting.class-end.hour}") int cEndHour,
                         @Value("${schedule-setting.class-end.minute}") int cEndMinute
    ) {
        C1_TIME = LocalTime.of(c1Hour, c1Minute);
        C2_TIME = LocalTime.of(c2Hour, c2Minute);
        C3_TIME = LocalTime.of(c3Hour, c3Minute);
        C4_TIME = LocalTime.of(c4Hour, c4Minute);
        C5_TIME = LocalTime.of(c5Hour, c5Minute);
        C6_TIME = LocalTime.of(c6Hour, c6Minute);
        C7_TIME = LocalTime.of(c7Hour, c7Minute);
        C8_TIME = LocalTime.of(c8Hour, c8Minute);
        C9_TIME = LocalTime.of(c9Hour, c9Minute);
        C10_TIME = LocalTime.of(c10Hour, c10Minute);
        C11_TIME = LocalTime.of(c11Hour, c11Minute);
        C12_TIME = LocalTime.of(c12Hour, c12Minute);
        END_TIME = LocalTime.of(cEndHour, cEndMinute);
        START_DATE = LocalDate.of(year, month, day);
    }

    /**
     * 转换LocalDateTime对象为Schedule 排课对象
     *
     * @param localDateTime 日期时间
     * @return 转换结果 Schedule
     */
    public Schedule getSchedule(LocalDateTime localDateTime) {
        Schedule schedule = new Schedule();
        schedule.setSchYear(getYear(localDateTime));
        schedule.setSchDay(getDay(localDateTime));
        schedule.setSchFortnight(getFortnight(localDateTime));
        schedule.setSchTerm(getTerm(localDateTime));
        return schedule;
    }

    public Integer getYear(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            int year = localDateTime.getYear();
            int month = localDateTime.getMonthValue();
            if (month < 8) {
                return year - 1;
            } else {
                return year;
            }
        } else {
            return null;
        }
    }

    /**
     * 获得第几周
     *
     * @param localDateTime 日期时间
     * @return 周数
     */
    public Integer getWeek(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            long days = START_DATE.until(localDateTime, ChronoUnit.DAYS);
            return (int) (days / 7) + 1;
        } else {
            return null;
        }
    }

    /**
     * 获得dayOfWeek
     *
     * @param localDateTime 日期时间
     * @return dayOfWeek
     */
    public Integer getDay(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return localDateTime.getDayOfWeek().getValue();
        } else {
            return null;
        }
    }

    /**
     * 获得单双周
     *
     * @param localDateTime 日期时间
     * @return 1 = 单周, 2 = 双周
     */
    public Byte getFortnight(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return (byte) ((getWeek(localDateTime) % 2 == 0) ? 2 : 1);
        } else {
            return null;
        }
    }

    /**
     * 获得学期
     *
     * @param localDateTime 日期时间
     * @return false = 上学期, true =  下学期
     */
    public Boolean getTerm(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            int month = localDateTime.getMonthValue();
            return (month <= 8 && month >= 2);
        } else {
            return null;
        }
    }

    /**
     * 获得第几节课
     *
     * @param localDateTime 日期时间
     * @return 课数
     */
    public Integer getTime(LocalDateTime localDateTime) {
        if (localDateTime != null) {
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
        } else {
            return null;
        }
    }

    /**
     * 获得课程开始时间
     *
     * @param time 第几节
     * @return localTime 课程开始时间
     */
    public LocalTime getTime(int time) {
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
