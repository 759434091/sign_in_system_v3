package team.a9043.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人工签到 类
 */
public class SuvMan implements Serializable {
    private static final long serialVersionUID = -2729082378706510489L;
    private int suvManId;
    private int siWeek;
    private int schId;
    private LocalDateTime siTime;
    private boolean suvManAutoOpen;

    public SuvMan() {
    }

    public SuvMan(int schId, int siWeek, LocalDateTime siTime) {
        this.siWeek = siWeek;
        this.schId = schId;
        this.siTime = siTime;
    }

    public int getSuvManId() {
        return suvManId;
    }

    public SuvMan setSuvManId(int suvManId) {
        this.suvManId = suvManId;
        return this;
    }

    public int getSiWeek() {
        return siWeek;
    }

    public SuvMan setSiWeek(int siWeek) {
        this.siWeek = siWeek;
        return this;
    }

    public int getSchId() {
        return schId;
    }

    public SuvMan setSchId(int schId) {
        this.schId = schId;
        return this;
    }

    public LocalDateTime getSiTime() {
        return siTime;
    }

    public SuvMan setSiTime(LocalDateTime siTime) {
        this.siTime = siTime;
        return this;
    }

    public boolean isSuvManAutoOpen() {
        return suvManAutoOpen;
    }

    public SuvMan setSuvManAutoOpen(boolean suvManAutoOpen) {
        this.suvManAutoOpen = suvManAutoOpen;
        return this;
    }
}
