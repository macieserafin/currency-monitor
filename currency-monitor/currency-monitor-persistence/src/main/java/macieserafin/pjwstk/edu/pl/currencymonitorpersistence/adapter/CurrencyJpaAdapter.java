package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.adapter;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.model.CurrencySnapshot;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.CurrencyPort;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.Currency;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.CurrencyRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrencyJpaAdapter implements CurrencyPort {

    private final CurrencyRepository currencyRepository;

    public CurrencyJpaAdapter(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public Optional<CurrencySnapshot> findByCode(String code) {
        return currencyRepository.findByCode(code)
                .map(c -> new CurrencySnapshot(c.getCode(), c.getName()));
    }

    @Override
    public CurrencySnapshot save(CurrencySnapshot currency) {
        Currency entity = new Currency();
        entity.setCode(currency.getCode());
        entity.setName(currency.getName());
        currencyRepository.save(entity);
        return currency;
    }
}