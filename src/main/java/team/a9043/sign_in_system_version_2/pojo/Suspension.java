package team.a9043.sign_in_system_version_2.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Suspension {
    private int susId;
    private int susWeek;
    private int scheduleSchId;

    public int getSusId() {
        return susId;
    }

    public Suspension setSusId(int susId) {
        this.susId = susId;
        return this;
    }

    public int getSusWeek() {
        return susWeek;
    }

    public Suspension setSusWeek(int susWeek) {
        this.susWeek = susWeek;
        return this;
    }

    public int getScheduleSchId() {
        return scheduleSchId;
    }

    public Suspension setScheduleSchId(int scheduleSchId) {
        this.scheduleSchId = scheduleSchId;
        return this;
    }
}
