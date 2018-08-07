package team.a9043.sign_in_system.util.judgetime;

import team.a9043.sign_in_system.entity.SisSchedule;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * @author a9043
 */
@SuppressWarnings("WeakerAccess")
public class JudgeTimeUtil {
    private static Month splitMonth = Month.NOVEMBER;
    private static LocalDate startDate = LocalDate.of(2018, splitMonth, 1);
    private static int classDuration = 45;
    private static LocalTime firstClass = LocalTime.of(8, 30);
    private static LocalTime secondClass = LocalTime.of(9, 20);
    private static LocalTime thirdClass = LocalTime.of(10, 20);
    private static LocalTime fourthClass = LocalTime.of(11, 10);
    private static LocalTime fifthClass = LocalTime.of(14, 30);
    private static LocalTime sixthClass = LocalTime.of(15, 20);
    private static LocalTime seventhClass = LocalTime.of(16, 20);
    private static LocalTime eighthClass = LocalTime.of(17, 10);
    private static LocalTime ninthClass = LocalTime.of(19, 30);
    private static LocalTime tenthClass = LocalTime.of(20, 20);
    private static LocalTime eleventhClass = LocalTime.of(21, 10);
    private static LocalTime twelfthClass = LocalTime.of(22, 0);


    public static void changeParams(Month month,
                                    LocalDate sDate,
                                    int cDuration) {
        classDuration = cDuration;
        startDate = sDate;
        splitMonth = month;
    }

    public static boolean isCourseTime(SisSchedule sisSchedule,
                                       int actionWeek,
                                       LocalDateTime localDateTime) throws ScheduleParerException {
        boolean res1 = judgeYearEtTerm(localDateTime,
            sisSchedule.getSsYearEtTerm());

        boolean res2 = judgeWeekEtFortnight(localDateTime,
            actionWeek,
            sisSchedule.getSsStartWeek(),
            sisSchedule.getSsEndWeek(),
            sisSchedule.getSsFortnight());

        boolean res3 = judgeDayOfWeek(localDateTime,
            sisSchedule.getSsDayOfWeek());

        boolean res4 = judgeTime(localDateTime,
            sisSchedule.getSsStartTime(),
            sisSchedule.getSsEndTime());

        return res1 && res2 && res3 && res4;
    }

    public static boolean judgeYearEtTerm(LocalDateTime localDateTime,
                                          String ssYearEtTerm) throws ScheduleParerException {
        int termYear =
            localDateTime.getMonth().getValue() >= splitMonth.getValue() ?
                localDateTime.getYear() : localDateTime.getYear() - 1;
        SisSchedule.SsTerm term =
            localDateTime.getMonth().getValue() >= splitMonth.getValue() ?
                SisSchedule.SsTerm.FIRST : SisSchedule.SsTerm.SECOND;
        int stdStartYear;
        int stdEndYear;
        SisSchedule.SsTerm stdTerm;
        try {
            String[] yearTerms = ssYearEtTerm.split("-");
            stdStartYear = Integer.valueOf(yearTerms[0]);
            stdEndYear = Integer.valueOf(yearTerms[1]);
            stdTerm = SisSchedule.SsTerm.toEnum(Integer.valueOf(yearTerms[2]));

            if (stdStartYear <= 2000 ||
                stdStartYear >= 3000 ||
                stdEndYear <= 2000 ||
                stdEndYear >= 3000 ||
                null == stdTerm)
                throw new NumberFormatException();
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new ScheduleParerException(
                String.format("error: %s cause: %s",
                    ssYearEtTerm,
                    e.getMessage()));
        }

        return stdStartYear == termYear && stdTerm.equals(term);
    }

    public static boolean judgeWeekEtFortnight(LocalDateTime localDateTime,
                                               int actionWeek,
                                               int stdSsStartWeek,
                                               int stdSsEndWeek,
                                               SisSchedule.SsFortnight stdSsFortnight) {
        int nowWeek =
            (Math.toIntExact(localDateTime
                .toLocalDate().until(startDate, ChronoUnit.DAYS)) - 1) / 7 + 1;
        if (nowWeek != actionWeek || nowWeek < stdSsStartWeek || nowWeek > stdSsEndWeek)
            return false;

        SisSchedule.SsFortnight ssFortnight = (nowWeek % 2) == 0 ?
            SisSchedule.SsFortnight.EVEN : SisSchedule.SsFortnight.ODD;
        return stdSsFortnight.equals(SisSchedule.SsFortnight.FULL) ||
            stdSsFortnight.equals(ssFortnight);
    }

    public static boolean judgeDayOfWeek(LocalDateTime localDateTime,
                                         DayOfWeek dayOfWeek) {
        return dayOfWeek == localDateTime.getDayOfWeek();
    }

    public static boolean judgeTime(LocalDateTime localDateTime,
                                    int ssStartTime,
                                    int ssEndTime) {
        // judge time
        LocalTime localTime = localDateTime.toLocalTime();
        LocalTime stdStartTime = getClassTime(ssStartTime);
        LocalTime stdEndTime = getClassTime(ssEndTime);
        return null != stdStartTime &&
            null != stdEndTime &&
            !localTime.isBefore(stdStartTime) &&
            !localTime.isAfter(stdEndTime.plus(classDuration,
                ChronoUnit.MINUTES));
    }

    public static LocalTime getClassTime(int value) {
        switch (value) {
            case 1:
                return firstClass;
            case 2:
                return secondClass;
            case 3:
                return thirdClass;
            case 4:
                return fourthClass;
            case 5:
                return fifthClass;
            case 6:
                return sixthClass;
            case 7:
                return seventhClass;
            case 8:
                return eighthClass;
            case 9:
                return ninthClass;
            case 10:
                return tenthClass;
            case 11:
                return eleventhClass;
            case 12:
                return twelfthClass;
            default:
                return null;
        }
    }
}
