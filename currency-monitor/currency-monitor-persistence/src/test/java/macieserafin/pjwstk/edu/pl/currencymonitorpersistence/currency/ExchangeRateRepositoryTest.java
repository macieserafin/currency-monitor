package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExchangeRateRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Test
    void shouldFindByCurrencyAndDate() {
        // given
        Currency usd = new Currency("USD", "Dollar");
        currencyRepository.save(usd);

        ExchangeRate rate = new ExchangeRate(
                usd,
                new BigDecimal("4.1234"),
                LocalDate.of(2024, 1, 10)
        );
        exchangeRateRepository.save(rate);

        // when
        var found = exchangeRateRepository.findByCurrencyAndDate(
                usd,
                LocalDate.of(2024, 1, 10)
        );

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getRate()).isEqualByComparingTo("4.1234");
        assertThat(found.get().getCurrency().getCode()).isEqualTo("USD");
    }

    @Test
    void shouldReturnRatesOrderedByDateDesc() {
        // given
        Currency eur = new Currency("EUR", "Euro");
        currencyRepository.save(eur);

        exchangeRateRepository.save(
                new ExchangeRate(eur, new BigDecimal("4.10"), LocalDate.of(2024, 1, 1))
        );
        exchangeRateRepository.save(
                new ExchangeRate(eur, new BigDecimal("4.20"), LocalDate.of(2024, 1, 5))
        );
        exchangeRateRepository.save(
                new ExchangeRate(eur, new BigDecimal("4.30"), LocalDate.of(2024, 1, 10))
        );

        // when
        List<ExchangeRate> rates =
                exchangeRateRepository.findByCurrencyOrderByDateDesc(eur);

        // then
        assertThat(rates).hasSize(3);
        assertThat(rates.get(0).getDate()).isEqualTo(LocalDate.of(2024, 1, 10));
        assertThat(rates.get(1).getDate()).isEqualTo(LocalDate.of(2024, 1, 5));
        assertThat(rates.get(2).getDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    }
}