package macieserafin.pjwstk.edu.pl.currencymonitorapp.currency;



import macieserafin.pjwstk.edu.pl.currencymonitorapp.api.ExchangeRateDto;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.CurrencyRepository;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.ExchangeRateRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeRateService {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository rateRepository;

    public ExchangeRateService(
            CurrencyRepository currencyRepository,
            ExchangeRateRepository rateRepository
    ) {
        this.currencyRepository = currencyRepository;
        this.rateRepository = rateRepository;
    }

    @Cacheable(value="exchangeRates", key="#p0", condition="#p0 != null && !#p0.isBlank()")
    public List<ExchangeRateDto> getRatesForCurrency(String code) {

        var currency = currencyRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Currency not found"));

        return rateRepository.findByCurrencyOrderByDateDesc(currency)
                .stream()
                .map(r -> new ExchangeRateDto(r.getDate(), r.getRate()))
                .toList();
    }
}