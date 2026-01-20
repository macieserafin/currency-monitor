package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency;

import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.user.User;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ObservedCurrencyRepositoryTest {

    @Autowired
    private ObservedCurrencyRepository observedCurrencyRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindByUserAndCurrency() {
        // given
        User user = userRepository.save(new User("testuser", "password"));
        Currency currency = currencyRepository.save(new Currency("USD", "Dollar"));

        ObservedCurrency observed =
                observedCurrencyRepository.save(new ObservedCurrency(user, currency));

        // when
        var found =
                observedCurrencyRepository.findByUserAndCurrency(user, currency);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
        assertThat(found.get().getCurrency().getCode()).isEqualTo("USD");
    }

    @Test
    void shouldFindAllObservedCurrenciesByUser() {
        // given
        User user = userRepository.save(new User("testuser", "password"));

        Currency usd = currencyRepository.save(new Currency("USD", "Dollar"));
        Currency eur = currencyRepository.save(new Currency("EUR", "Euro"));

        observedCurrencyRepository.save(new ObservedCurrency(user, usd));
        observedCurrencyRepository.save(new ObservedCurrency(user, eur));

        // when
        var result = observedCurrencyRepository.findByUser(user);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(o -> o.getCurrency().getCode())
                .containsExactlyInAnyOrder("USD", "EUR");
    }
}