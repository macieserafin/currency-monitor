package macieserafin.pjwstk.edu.pl.currencymonitor.api;

import macieserafin.pjwstk.edu.pl.currencymonitor.currency.CurrencyRepository;
import macieserafin.pjwstk.edu.pl.currencymonitor.currency.ExchangeRateRepository;
import macieserafin.pjwstk.edu.pl.currencymonitor.currency.ExchangeRateService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/{code}")
    public List<ExchangeRateDto> getRatesForCurrency(
            @PathVariable("code") String code
    ) {
        return exchangeRateService.getRatesForCurrency(code);
    }
}
