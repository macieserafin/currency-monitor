package macieserafin.pjwstk.edu.pl.currencymonitor.api;

import macieserafin.pjwstk.edu.pl.currencymonitor.currency.CurrencyRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final CurrencyRepository currencyRepository;

    public CurrencyController(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @GetMapping
    public List<CurrencyDto> getAllCurrencies() {
        return currencyRepository.findAll()
                .stream()
                .map(c -> new CurrencyDto(c.getCode(), c.getName()))
                .toList();
    }
}
