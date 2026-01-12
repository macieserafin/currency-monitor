package macieserafin.pjwstk.edu.pl.currencymonitorcore.port;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateProvider {

    ExchangeRateTable fetchCurrentRates();

    record ExchangeRateTable(
            LocalDate effectiveDate,
            List<ExchangeRateEntry> rates
    ) {}

    record ExchangeRateEntry(
            String currency,
            String code,
            java.math.BigDecimal mid
    ) {}
}
