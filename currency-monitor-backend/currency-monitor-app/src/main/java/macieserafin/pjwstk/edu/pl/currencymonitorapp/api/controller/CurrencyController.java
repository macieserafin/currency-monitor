package macieserafin.pjwstk.edu.pl.currencymonitorapp.api.controller;

import macieserafin.pjwstk.edu.pl.currencymonitorapp.api.dto.CurrencyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.Currency;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.CurrencyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {
    private static final Logger log =
            LoggerFactory.getLogger(CurrencyController.class);

    private final CurrencyRepository currencyRepository;

    public CurrencyController(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @GetMapping
    public List<CurrencyDto> getAllCurrencies() {
        return currencyRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private CurrencyDto mapToDto(Currency currency) {
        CurrencyDto dto = new CurrencyDto();
        dto.setCode(currency.getCode());
        dto.setName(currency.getName());
        return dto;
    }


}
