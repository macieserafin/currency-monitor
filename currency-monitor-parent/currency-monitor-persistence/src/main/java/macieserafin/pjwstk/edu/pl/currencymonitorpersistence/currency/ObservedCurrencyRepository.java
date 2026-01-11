package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency;

import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObservedCurrencyRepository extends JpaRepository<ObservedCurrency, Long> {

    Optional<ObservedCurrency> findByUserAndCurrency(User user, Currency currency);

    List<ObservedCurrency> findByUser(User user);
}
