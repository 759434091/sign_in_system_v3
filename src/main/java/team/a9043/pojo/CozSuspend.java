package team.a9043.pojo;

import java.io.Serializable;

public class CozSuspend implements Serializable {
    private static final long serialVersionUID = -7819528624995286652L;
    private int susId;
    private int schId;
    private int susWeek;

    public int getSusId() {
        return susId;
    }

    public CozSuspend setSusId(int susId) {
        this.susId = susId;
        return this;
    }

    public int getSchId() {
        return schId;
    }

    public CozSuspend setSchId(int schId) {
        this.schId = schId;
        return this;
    }

    public int getSusWeek() {
        return susWeek;
    }

    public CozSuspend setSusWeek(int susWeek) {
        this.susWeek = susWeek;
        return this;
    }
}
