package macieserafin.pjwstk.edu.pl.currencymonitorcore.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeRateSnapshot {

    private final LocalDate date;
    private final BigDecimal rate;

    public ExchangeRateSnapshot(LocalDate date, BigDecimal rate) {
        this.date = date;
        this.rate = rate;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
