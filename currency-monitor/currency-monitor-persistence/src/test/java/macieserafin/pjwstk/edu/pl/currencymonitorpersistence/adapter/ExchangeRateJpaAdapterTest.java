package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.adapter;

import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExchangeRateJpaAdapterTest {

    @Test
    void shouldReturnTrueIfRateExistsForDate() {
        CurrencyRepository currencyRepo = mock(CurrencyRepository.class);
        ExchangeRateRepository rateRepo = mock(ExchangeRateRepository.class);

        ExchangeRateJpaAdapter adapter =
                new ExchangeRateJpaAdapter(rateRepo, currencyRepo);

        Currency currency = new Currency("USD", "Dollar");
        when(currencyRepo.findByCode("USD")).thenReturn(Optional.of(currency));
        when(rateRepo.findByCurrencyAndDate(currency, LocalDate.now()))
                .thenReturn(Optional.of(new ExchangeRate()));

        boolean exists = adapter.existsForDate("USD", LocalDate.now());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldSaveExchangeRate() {
        CurrencyRepository currencyRepo = mock(CurrencyRepository.class);
        ExchangeRateRepository rateRepo = mock(ExchangeRateRepository.class);

        ExchangeRateJpaAdapter adapter =
                new ExchangeRateJpaAdapter(rateRepo, currencyRepo);

        Currency currency = new Currency("USD", "Dollar");
        when(currencyRepo.findByCode("USD")).thenReturn(Optional.of(currency));

        adapter.save("USD", LocalDate.now(), BigDecimal.TEN);

        verify(rateRepo).save(any(ExchangeRate.class));
    }

    @Test
    void shouldFindRatesByCurrencyCode() {
        CurrencyRepository currencyRepo = mock(CurrencyRepository.class);
        ExchangeRateRepository rateRepo = mock(ExchangeRateRepository.class);

        ExchangeRateJpaAdapter adapter =
                new ExchangeRateJpaAdapter(rateRepo, currencyRepo);

        Currency currency = new Currency("USD", "Dollar");
        when(currencyRepo.findByCode("USD")).thenReturn(Optional.of(currency));
        when(rateRepo.findByCurrencyOrderByDateDesc(currency))
                .thenReturn(List.of(new ExchangeRate(currency, BigDecimal.ONE, LocalDate.now())));

        var result = adapter.findByCurrencyCode("USD");

        assertThat(result).hasSize(1);
    }
}