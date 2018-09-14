package team.a9043.pojo;

import java.io.Serializable;

/**
 * 地点对象
 */
public class Location implements Serializable {
    private static final long serialVersionUID = -7682512842650733832L;
    private int locId;
    private double locLat;
    private double locLon;
    private double locAcc;
    private double locVAcc;
    private double locHAcc;
    private String locName;

    public Location() {
    }

    public Location(int locId, double locLat, double locLon, double locAcc, double locVAcc, double locHAcc, String locName) {
        this.locId = locId;
        this.locLat = locLat;
        this.locLon = locLon;
        this.locAcc = locAcc;
        this.locVAcc = locVAcc;
        this.locHAcc = locHAcc;
        this.locName = locName;
    }

    public int getLocId() {
        return locId;
    }

    public Location setLocId(int locId) {
        this.locId = locId;
        return this;
    }

    public String getLocName() {
        return locName;
    }

    public Location setLocName(String locName) {
        this.locName = locName;
        return this;
    }

    public double getLocLat() {
        return locLat;
    }

    public Location setLocLat(double locLat) {
        this.locLat = locLat;
        return this;
    }

    public double getLocLon() {
        return locLon;
    }

    public Location setLocLon(double locLon) {
        this.locLon = locLon;
        return this;
    }

    public double getLocAcc() {
        return locAcc;
    }

    public Location setLocAcc(double locAcc) {
        this.locAcc = locAcc;
        return this;
    }

    public double getLocVAcc() {
        return locVAcc;
    }

    public Location setLocVAcc(double locVAcc) {
        this.locVAcc = locVAcc;
        return this;
    }

    public double getLocHAcc() {
        return locHAcc;
    }

    public Location setLocHAcc(double locHAcc) {
        this.locHAcc = locHAcc;
        return this;
    }
}
