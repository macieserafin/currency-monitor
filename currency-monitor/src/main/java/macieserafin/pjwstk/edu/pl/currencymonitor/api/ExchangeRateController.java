package macieserafin.pjwstk.edu.pl.currencymonitor.api;

import macieserafin.pjwstk.edu.pl.currencymonitor.currency.CurrencyRepository;
import macieserafin.pjwstk.edu.pl.currencymonitor.currency.ExchangeRateRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rates")
public class ExchangeRateController {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository rateRepository;

    public ExchangeRateController(
            CurrencyRepository currencyRepository,
            ExchangeRateRepository rateRepository
    ) {
        this.currencyRepository = currencyRepository;
        this.rateRepository = rateRepository;
    }

    @Cacheable("exchangeRates")
    @GetMapping("/{code}")
    public List<ExchangeRateDto> getRatesForCurrency(
            @PathVariable("code") String code
    ) {
        System.out.println("FETCHING FROM DB for " + code);

        var currency = currencyRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Currency not found"));

        return rateRepository.findByCurrencyOrderByDateDesc(currency)
                .stream()
                .map(r -> new ExchangeRateDto(r.getDate(), r.getRate()))
                .toList();
    }
}
