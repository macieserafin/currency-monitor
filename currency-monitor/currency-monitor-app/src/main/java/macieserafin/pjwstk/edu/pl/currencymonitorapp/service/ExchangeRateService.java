package macieserafin.pjwstk.edu.pl.currencymonitorapp.service;

import macieserafin.pjwstk.edu.pl.currencymonitorapp.api.dto.ExchangeRateDto;
import macieserafin.pjwstk.edu.pl.currencymonitorapp.api.exception.CurrencyNotFoundException;
import macieserafin.pjwstk.edu.pl.currencymonitorapp.api.exception.ExchangeRateNotFoundException;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.Currency;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.CurrencyRepository;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.ExchangeRate;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.ExchangeRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeRateService {

    private static final Logger log =
            LoggerFactory.getLogger(ExchangeRateService.class);

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository rateRepository;

    public ExchangeRateService(
            CurrencyRepository currencyRepository,
            ExchangeRateRepository rateRepository
    ) {
        this.currencyRepository = currencyRepository;
        this.rateRepository = rateRepository;
    }

    @Cacheable(
            value = "exchangeRates",
            key = "#code",
            condition = "#code != null && !#code.isBlank()"
    )
    public List<ExchangeRateDto> getRatesForCurrency(String code) {

        log.info("Fetching exchange rates for currency code={}", code);

        Currency currency = currencyRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> {
                    log.warn("Currency not found for code={}", code);
                    return new CurrencyNotFoundException(code);
                });

        var rates = rateRepository.findByCurrencyOrderByDateDesc(currency);

        if (rates.isEmpty()) {
            log.warn("No exchange rates found for currency code={}", code);
            throw new ExchangeRateNotFoundException(code);
        }

        log.info("Found {} exchange rates for currency code={}", rates.size(), code);

        return rates.stream()
                .map(this::mapToDto)
                .toList();
    }

    private ExchangeRateDto mapToDto(ExchangeRate rate) {
        ExchangeRateDto dto = new ExchangeRateDto();
        dto.setDate(rate.getDate());
        dto.setRate(rate.getRate());
        return dto;
    }
}