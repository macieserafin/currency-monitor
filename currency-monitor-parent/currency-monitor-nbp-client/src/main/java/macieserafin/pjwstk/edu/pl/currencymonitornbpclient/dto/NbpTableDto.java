package macieserafin.pjwstk.edu.pl.currencymonitornbpclient.dto;

import java.util.List;

public class NbpTableDto {

    private String table;
    private String effectiveDate;
    private List<NbpRateDto> rates;

    public String getTable() {
        return table;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public List<NbpRateDto> getRates() {
        return rates;
    }
}
