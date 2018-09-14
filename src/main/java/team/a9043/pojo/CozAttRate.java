package team.a9043.pojo;

import java.math.BigDecimal;

public class CozAttRate {
    private String cozId;
    private BigDecimal cozAttRate;

    public String getCozId() {
        return cozId;
    }

    public CozAttRate setCozId(String cozId) {
        this.cozId = cozId;
        return this;
    }

    public BigDecimal getCozAttRate() {
        return cozAttRate;
    }

    public CozAttRate setCozAttRate(BigDecimal cozAttRate) {
        this.cozAttRate = cozAttRate;
        return this;
    }
}
