package macieserafin.pjwstk.edu.pl.currencymonitorcore.usecase;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.model.CurrencySnapshot;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ApiFetchLogPort;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.CurrencyPort;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRatePort;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRateProvider;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRateProvider.ExchangeRateEntry;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRateProvider.ExchangeRateTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class UpdateExchangeRatesUseCaseTest {

    private CurrencyPort currencyPort;
    private ExchangeRatePort exchangeRatePort;
    private ApiFetchLogPort apiFetchLogPort;
    private ExchangeRateProvider exchangeRateProvider;

    private UpdateExchangeRatesUseCase useCase;

    @BeforeEach
    void setUp() {
        currencyPort = mock(CurrencyPort.class);
        exchangeRatePort = mock(ExchangeRatePort.class);
        apiFetchLogPort = mock(ApiFetchLogPort.class);
        exchangeRateProvider = mock(ExchangeRateProvider.class);

        useCase = new UpdateExchangeRatesUseCase(
                currencyPort,
                exchangeRatePort,
                apiFetchLogPort,
                exchangeRateProvider
        );
    }

    @Test
    void shouldSaveCurrencyAndExchangeRateWhenNotExists() {
        // given
        LocalDate date = LocalDate.of(2024, 1, 1);

        ExchangeRateEntry entry = new ExchangeRateEntry(
                "US Dollar",
                "USD",
                new BigDecimal("4.00")
        );

        ExchangeRateTable table = new ExchangeRateTable(
                date,
                List.of(entry)
        );

        when(exchangeRateProvider.fetchCurrentRates()).thenReturn(table);
        when(currencyPort.findByCode("USD")).thenReturn(Optional.empty());

        CurrencySnapshot savedCurrency =
                new CurrencySnapshot("USD", "US Dollar");

        when(currencyPort.save(any())).thenReturn(savedCurrency);
        when(exchangeRatePort.existsForDate("USD", date)).thenReturn(false);

        // when
        useCase.update();

        // then
        verify(currencyPort).save(any(CurrencySnapshot.class));
        verify(exchangeRatePort).save("USD", date, new BigDecimal("4.00"));
        verify(apiFetchLogPort).logSuccess(anyString());
        verify(apiFetchLogPort, never()).logError(anyString());
    }

    @Test
    void shouldNotSaveExchangeRateIfAlreadyExists() {
        // given
        LocalDate date = LocalDate.of(2024, 1, 1);

        ExchangeRateEntry entry = new ExchangeRateEntry(
                "Euro",
                "EUR",
                new BigDecimal("4.50")
        );

        ExchangeRateTable table = new ExchangeRateTable(
                date,
                List.of(entry)
        );

        CurrencySnapshot currency =
                new CurrencySnapshot("EUR", "Euro");

        when(exchangeRateProvider.fetchCurrentRates()).thenReturn(table);
        when(currencyPort.findByCode("EUR")).thenReturn(Optional.of(currency));
        when(exchangeRatePort.existsForDate("EUR", date)).thenReturn(true);

        // when
        useCase.update();

        // then
        verify(exchangeRatePort, never()).save(any(), any(), any());
        verify(apiFetchLogPort).logSuccess(anyString());
        verify(apiFetchLogPort, never()).logError(anyString());
    }

    @Test
    void shouldLogErrorWhenExceptionOccurs() {
        // given
        when(exchangeRateProvider.fetchCurrentRates())
                .thenThrow(new RuntimeException("NBP API down"));

        // when
        useCase.update();

        // then
        verify(apiFetchLogPort).logError("NBP API down");
        verify(apiFetchLogPort, never()).logSuccess(anyString());
    }
}