package macieserafin.pjwstk.edu.pl.currencymonitorcore.port;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.model.ExchangeRateSnapshot;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRatePort {

    boolean existsForDate(String currencyCode, LocalDate date);
    void save(String currencyCode, LocalDate date, java.math.BigDecimal rate);

    List<ExchangeRateSnapshot> findByCurrencyCode(String currencyCode);
}
