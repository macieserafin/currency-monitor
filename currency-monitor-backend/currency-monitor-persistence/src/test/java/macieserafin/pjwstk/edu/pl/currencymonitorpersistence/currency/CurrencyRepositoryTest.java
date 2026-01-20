package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    void shouldFindByCode() {
        // given
        Currency currency = new Currency("USD", "Dollar");
        currencyRepository.save(currency);

        // when
        var found = currencyRepository.findByCode("USD");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Dollar");
        assertThat(found.get().getCode()).isEqualTo("USD");
    }
}