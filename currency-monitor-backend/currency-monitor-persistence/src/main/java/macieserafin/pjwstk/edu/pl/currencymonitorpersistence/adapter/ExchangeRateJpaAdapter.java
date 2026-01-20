package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.adapter;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.model.ExchangeRateSnapshot;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRatePort;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.Currency;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.CurrencyRepository;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.ExchangeRate;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.ExchangeRateRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class ExchangeRateJpaAdapter implements ExchangeRatePort {

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyRepository currencyRepository;

    public ExchangeRateJpaAdapter(
            ExchangeRateRepository exchangeRateRepository,
            CurrencyRepository currencyRepository
    ) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public boolean existsForDate(String currencyCode, LocalDate date) {
        Currency currency = currencyRepository.findByCode(currencyCode)
                .orElseThrow();
        return exchangeRateRepository.findByCurrencyAndDate(currency, date).isPresent();
    }

    @Override
    public void save(String currencyCode, LocalDate date, BigDecimal rate) {
        Currency currency = currencyRepository.findByCode(currencyCode)
                .orElseThrow();

        ExchangeRate entity = new ExchangeRate();
        entity.setCurrency(currency);
        entity.setDate(date);
        entity.setRate(rate);

        exchangeRateRepository.save(entity);
    }

    @Override
    public List<ExchangeRateSnapshot> findByCurrencyCode(String currencyCode) {
        Currency currency = currencyRepository.findByCode(currencyCode)
                .orElseThrow();

        return exchangeRateRepository
                .findByCurrencyOrderByDateDesc(currency)
                .stream()
                .map(e -> new ExchangeRateSnapshot(e.getDate(), e.getRate()))
                .toList();
    }
}
