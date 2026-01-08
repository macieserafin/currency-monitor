package macieserafin.pjwstk.edu.pl.currencymonitor.dto;

import java.math.BigDecimal;

public class NbpRateDto {

    private String currency;
    private String code;
    private BigDecimal mid;

    public String getCurrency() {
        return currency;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getMid() {
        return mid;
    }
}
