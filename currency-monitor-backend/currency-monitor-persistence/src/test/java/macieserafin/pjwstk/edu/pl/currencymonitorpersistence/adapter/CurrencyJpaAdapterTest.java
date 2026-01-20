package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.adapter;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.model.CurrencySnapshot;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.Currency;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.CurrencyRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CurrencyJpaAdapterTest {

    @Test
    void shouldFindByCode() {
        CurrencyRepository repo = mock(CurrencyRepository.class);
        CurrencyJpaAdapter adapter = new CurrencyJpaAdapter(repo);

        Currency entity = new Currency("USD", "Dollar");
        when(repo.findByCode("USD")).thenReturn(Optional.of(entity));

        var result = adapter.findByCode("USD");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("USD");
        assertThat(result.get().getName()).isEqualTo("Dollar");
    }

    @Test
    void shouldSaveCurrency() {
        CurrencyRepository repo = mock(CurrencyRepository.class);
        CurrencyJpaAdapter adapter = new CurrencyJpaAdapter(repo);

        CurrencySnapshot snapshot = new CurrencySnapshot("EUR", "Euro");

        adapter.save(snapshot);

        verify(repo).save(any(Currency.class));
    }
}