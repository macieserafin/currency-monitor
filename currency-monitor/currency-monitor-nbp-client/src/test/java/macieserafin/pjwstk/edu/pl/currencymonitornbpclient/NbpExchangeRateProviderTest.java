package macieserafin.pjwstk.edu.pl.currencymonitornbpclient;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRateProvider;
import macieserafin.pjwstk.edu.pl.currencymonitornbpclient.dto.NbpRateDto;
import macieserafin.pjwstk.edu.pl.currencymonitornbpclient.dto.NbpTableDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NbpExchangeRateProviderTest {

    @Test
    void shouldMapNbpResponseToExchangeRateTable() {
        // given
        NbpClient nbpClient = mock(NbpClient.class);
        ExchangeRateProvider provider = new NbpExchangeRateProvider(nbpClient);

        NbpRateDto rate = new NbpRateDto();
        setField(rate, "currency", "euro");
        setField(rate, "code", "EUR");
        setField(rate, "mid", new BigDecimal("4.50"));

        NbpTableDto table = new NbpTableDto();
        setField(table, "effectiveDate", "2024-01-01");
        setField(table, "rates", List.of(rate));

        when(nbpClient.fetchCurrentRates()).thenReturn(new NbpTableDto[]{table});

        // when
        var result = provider.fetchCurrentRates();

        // then
        assertThat(result.effectiveDate()).isEqualTo(java.time.LocalDate.of(2024, 1, 1));
        assertThat(result.rates()).hasSize(1);

        var entry = result.rates().get(0);
        assertThat(entry.code()).isEqualTo("EUR");
        assertThat(entry.currency()).isEqualTo("euro");
        assertThat(entry.mid()).isEqualByComparingTo("4.50");
    }

    /**
     * Helper do ustawiania pól bez setterów (DTO z Jacksona)
     */
    private static void setField(Object target, String field, Object value) {
        try {
            var f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}