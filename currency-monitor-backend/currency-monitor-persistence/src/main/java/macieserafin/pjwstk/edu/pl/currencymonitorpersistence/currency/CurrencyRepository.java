package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
//gengeruje implementacje autoamtycznie
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCode(String code);
}
