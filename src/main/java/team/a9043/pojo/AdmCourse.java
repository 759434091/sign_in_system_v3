package team.a9043.pojo;

import java.math.BigDecimal;

public class AdmCourse extends Course {
    private int cozSize;
    private int cozActSize;
    private BigDecimal cozAttRate;
    private String cozDepart;
    private Integer cozGrade;
    private String schIdList;

    public String getCozDepart() {
        return cozDepart;
    }

    public AdmCourse setCozDepart(String cozDepart) {
        this.cozDepart = cozDepart;
        return this;
    }

    public Integer getCozGrade() {
        return cozGrade;
    }

    public AdmCourse setCozGrade(Integer cozGrade) {
        this.cozGrade = cozGrade;
        return this;
    }

    public String getSchIdList() {
        return schIdList;
    }

    public AdmCourse setSchIdList(String schIdList) {
        this.schIdList = schIdList;
        return this;
    }

    public int getCozSize() {
        return cozSize;
    }

    public AdmCourse setCozSize(int cozSize) {
        this.cozSize = cozSize;
        return this;
    }

    public int getCozActSize() {
        return cozActSize;
    }

    public AdmCourse setCozActSize(int cozActSize) {
        this.cozActSize = cozActSize;
        return this;
    }

    public BigDecimal getCozAttRate() {
        return cozAttRate;
    }

    public AdmCourse setCozAttRate(BigDecimal cozAttRate) {
        this.cozAttRate = cozAttRate;
        return this;
    }
}
