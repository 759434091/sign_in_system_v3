package team.a9043.pojo;

/**
 * 督导记录类
 */
public class SuvRecord {
    private int suvRecId;
    private int suvId;
    private int suvRecNum;
    private int suvRecBadNum;
    private String suvRecInfo;
    private String suvRecName;
    private int suvWeek;

    public int getSuvRecId() {
        return suvRecId;
    }

    public SuvRecord setSuvRecId(int suvRecId) {
        this.suvRecId = suvRecId;
        return this;
    }

    public int getSuvId() {
        return suvId;
    }

    public SuvRecord setSuvId(int suvId) {
        this.suvId = suvId;
        return this;
    }

    public int getSuvRecNum() {
        return suvRecNum;
    }

    public SuvRecord setSuvRecNum(int suvRecNum) {
        this.suvRecNum = suvRecNum;
        return this;
    }

    public int getSuvRecBadNum() {
        return suvRecBadNum;
    }

    public SuvRecord setSuvRecBadNum(int suvRecBadNum) {
        this.suvRecBadNum = suvRecBadNum;
        return this;
    }

    public String getSuvRecInfo() {
        return suvRecInfo;
    }

    public SuvRecord setSuvRecInfo(String suvRecInfo) {
        this.suvRecInfo = suvRecInfo;
        return this;
    }

    public String getSuvRecName() {
        return suvRecName;
    }

    public SuvRecord setSuvRecName(String suvRecName) {
        this.suvRecName = suvRecName;
        return this;
    }

    public int getSuvWeek() {
        return suvWeek;
    }

    public SuvRecord setSuvWeek(int suvWeek) {
        this.suvWeek = suvWeek;
        return this;
    }
}
