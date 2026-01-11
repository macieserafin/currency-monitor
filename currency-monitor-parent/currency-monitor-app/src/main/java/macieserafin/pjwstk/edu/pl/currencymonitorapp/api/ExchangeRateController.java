package macieserafin.pjwstk.edu.pl.currencymonitorapp.api;

import macieserafin.pjwstk.edu.pl.currencymonitorapp.currency.ExchangeRateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
