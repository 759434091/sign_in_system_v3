package team.a9043.pojo;


import java.io.Serializable;
import java.util.List;

/**
 * 排课 类
 */
public class Schedule implements Serializable {
    private static final long serialVersionUID = 4378722454971264928L;
    private String cozId;
    private int schTime, schDay, schWeek, schYear, schFortnight, schId;
    private boolean schTerm;
    private Location location;
    private List<CozSuspend> cozSuspendList;

    public Schedule() {
    }

    public Schedule(String cozId, int schTime, int schDay, int schWeek, int schYear, int schFortnight, int schId, boolean schTerm, Location location, List<CozSuspend> cozSuspendList) {
        this.cozId = cozId;
        this.schTime = schTime;
        this.schDay = schDay;
        this.schWeek = schWeek;
        this.schYear = schYear;
        this.schFortnight = schFortnight;
        this.schId = schId;
        this.schTerm = schTerm;
        this.location = location;
        this.cozSuspendList = cozSuspendList;
    }

    public Location getLocation() {
        return location;
    }

    public Schedule setLocation(Location location) {
        this.location = location;
        return this;
    }

    public int getSchId() {
        return schId;
    }

    public Schedule setSchId(int schId) {
        this.schId = schId;
        return this;
    }

    public String getCozId() {
        return cozId;
    }

    public Schedule setCozId(String cozId) {
        this.cozId = cozId;
        return this;
    }

    public int getSchTime() {
        return schTime;
    }

    public Schedule setSchTime(int schTime) {
        this.schTime = schTime;
        return this;
    }

    public int getSchDay() {
        return schDay;
    }

    public Schedule setSchDay(int schDay) {
        this.schDay = schDay;
        return this;
    }

    public int getSchWeek() {
        return schWeek;
    }

    public Schedule setSchWeek(int schWeek) {
        this.schWeek = schWeek;
        return this;
    }

    public int getSchYear() {
        return schYear;
    }

    public Schedule setSchYear(int schYear) {
        this.schYear = schYear;
        return this;
    }

    public boolean isSchTerm() {
        return schTerm;
    }

    public Schedule setSchTerm(boolean schTerm) {
        this.schTerm = schTerm;
        return this;
    }

    public int getSchFortnight() {
        return schFortnight;
    }

    public Schedule setSchFortnight(int schFortnight) {
        this.schFortnight = schFortnight;
        return this;
    }

    public List<CozSuspend> getCozSuspendList() {
        return cozSuspendList;
    }

    public Schedule setCozSuspendList(List<CozSuspend> cozSuspendList) {
        this.cozSuspendList = cozSuspendList;
        return this;
    }
}
