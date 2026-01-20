package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByCurrencyAndDate(Currency currency, LocalDate date);

    List<ExchangeRate> findByCurrencyOrderByDateDesc(Currency currency);

    Optional<ExchangeRate> findFirstByCurrencyOrderByDateDesc(Currency currency);
}
