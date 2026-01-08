package macieserafin.pjwstk.edu.pl.currencymonitor.currency;

import jakarta.transaction.Transactional;
import macieserafin.pjwstk.edu.pl.currencymonitor.ApiFetchLog;
import macieserafin.pjwstk.edu.pl.currencymonitor.ApiFetchLogRepository;
import macieserafin.pjwstk.edu.pl.currencymonitor.NbpClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExchangeRateUpdateService {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ApiFetchLogRepository logRepository;
    private final NbpClient nbpClient;

    public ExchangeRateUpdateService(
            CurrencyRepository currencyRepository,
            ExchangeRateRepository exchangeRateRepository,
            ApiFetchLogRepository logRepository,
            NbpClient nbpClient
    ) {
        this.currencyRepository = currencyRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.logRepository = logRepository;
        this.nbpClient = nbpClient;
    }

    @Transactional
    public void updateRates() {
        try {
            var tables = nbpClient.fetchCurrentRates();
            var table = tables[0];
            LocalDate date = LocalDate.parse(table.getEffectiveDate());

            for (var rateDto : table.getRates()) {

                Currency currency = currencyRepository
                        .findByCode(rateDto.getCode())
                        .orElseGet(() ->
                                currencyRepository.save(
                                        new Currency(
                                                rateDto.getCode(),
                                                rateDto.getCurrency()
                                        )
                                )
                        );

                boolean exists = exchangeRateRepository
                        .findByCurrencyAndDate(currency, date)
                        .isPresent();

                if (!exists) {
                    exchangeRateRepository.save(
                            new ExchangeRate(
                                    currency,
                                    rateDto.getMid(),
                                    date
                            )
                    );
                }
            }

            logRepository.save(
                    new ApiFetchLog("SUCCESS", "Rates updated successfully")
            );

        } catch (Exception e) {
            logRepository.save(
                    new ApiFetchLog("ERROR", e.getMessage())
            );
        }
    }
}
