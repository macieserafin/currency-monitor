package macieserafin.pjwstk.edu.pl.currencymonitorapp;

import macieserafin.pjwstk.edu.pl.currencymonitorapp.api.dto.ExchangeRateDto;
import macieserafin.pjwstk.edu.pl.currencymonitorapp.api.exception.CurrencyNotFoundException;
import macieserafin.pjwstk.edu.pl.currencymonitorapp.api.exception.ExchangeRateNotFoundException;
import macieserafin.pjwstk.edu.pl.currencymonitorapp.service.ExchangeRateService;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.Currency;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.CurrencyRepository;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.ExchangeRate;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    CurrencyRepository currencyRepository;

    @Mock
    ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    ExchangeRateService exchangeRateService;

    @Test
    void shouldReturnExchangeRatesForExistingCurrency() {
        // given
        Currency currency = new Currency();
        currency.setCode("EUR");
        currency.setName("Euro");

        ExchangeRate rate = new ExchangeRate();
        rate.setCurrency(currency);
        rate.setDate(LocalDate.of(2024, 1, 1));
        rate.setRate(BigDecimal.valueOf(4.50));

        when(currencyRepository.findByCode("EUR"))
                .thenReturn(Optional.of(currency));

        when(exchangeRateRepository.findByCurrencyOrderByDateDesc(currency))
                .thenReturn(List.of(rate));

        // when
        List<ExchangeRateDto> result =
                exchangeRateService.getRatesForCurrency("eur");

        // then
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(4.50), result.get(0).getRate());
        assertEquals(LocalDate.of(2024, 1, 1), result.get(0).getDate());

        verify(currencyRepository).findByCode("EUR");
        verify(exchangeRateRepository)
                .findByCurrencyOrderByDateDesc(currency);
    }

    @Test
    void shouldThrowCurrencyNotFoundExceptionWhenCurrencyDoesNotExist() {
        // given
        when(currencyRepository.findByCode("USD"))
                .thenReturn(Optional.empty());

        // then
        assertThrows(
                CurrencyNotFoundException.class,
                () -> exchangeRateService.getRatesForCurrency("usd")
        );

        verify(currencyRepository).findByCode("USD");
        verifyNoInteractions(exchangeRateRepository);
    }

    @Test
    void shouldThrowExchangeRateNotFoundExceptionWhenNoRatesExist() {
        // given
        Currency currency = new Currency();
        currency.setCode("GBP");
        currency.setName("British Pound");

        when(currencyRepository.findByCode("GBP"))
                .thenReturn(Optional.of(currency));

        when(exchangeRateRepository.findByCurrencyOrderByDateDesc(currency))
                .thenReturn(List.of());

        // then
        assertThrows(
                ExchangeRateNotFoundException.class,
                () -> exchangeRateService.getRatesForCurrency("gbp")
        );

        verify(currencyRepository).findByCode("GBP");
        verify(exchangeRateRepository)
                .findByCurrencyOrderByDateDesc(currency);
    }
}
