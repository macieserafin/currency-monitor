package macieserafin.pjwstk.edu.pl.currencymonitorcore.port;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.model.CurrencySnapshot;

import java.util.Optional;

public interface CurrencyPort {

    Optional<CurrencySnapshot> findByCode(String code);

    CurrencySnapshot save(CurrencySnapshot currency);
}
