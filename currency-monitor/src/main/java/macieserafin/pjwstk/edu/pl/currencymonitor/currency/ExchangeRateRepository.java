package macieserafin.pjwstk.edu.pl.currencymonitor.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByCurrencyAndDate(Currency currency, LocalDate date);

    List<ExchangeRate> findByCurrencyOrderByDateDesc(Currency currency);
}
