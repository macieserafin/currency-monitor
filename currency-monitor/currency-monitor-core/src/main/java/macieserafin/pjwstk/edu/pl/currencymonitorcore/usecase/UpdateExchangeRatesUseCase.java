package macieserafin.pjwstk.edu.pl.currencymonitorcore.usecase;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.model.CurrencySnapshot;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ApiFetchLogPort;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.CurrencyPort;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRatePort;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRateProvider;

import java.time.LocalDate;

public class UpdateExchangeRatesUseCase {

    private final CurrencyPort currencyPort;
    private final ExchangeRatePort exchangeRatePort;
    private final ApiFetchLogPort apiFetchLogPort;
    private final ExchangeRateProvider exchangeRateProvider;

    public UpdateExchangeRatesUseCase(
            CurrencyPort currencyPort,
            ExchangeRatePort exchangeRatePort,
            ApiFetchLogPort apiFetchLogPort,
            ExchangeRateProvider exchangeRateProvider
    ) {
        this.currencyPort = currencyPort;
        this.exchangeRatePort = exchangeRatePort;
        this.apiFetchLogPort = apiFetchLogPort;
        this.exchangeRateProvider = exchangeRateProvider;
    }

    public void update() {
        try {
            var table = exchangeRateProvider.fetchCurrentRates();
            LocalDate date = table.effectiveDate();

            for (var rate : table.rates()) {

                var currency = currencyPort.findByCode(rate.code())
                        .orElseGet(() ->
                                currencyPort.save(
                                        new CurrencySnapshot(
                                                rate.code(),
                                                rate.currency()
                                        )
                                )
                        );

                boolean exists = exchangeRatePort.existsForDate(currency.getCode(), date);
                if (!exists) {
                    exchangeRatePort.save(
                            currency.getCode(),
                            date,
                            rate.mid()
                    );
                }
            }

            apiFetchLogPort.logSuccess("Exchange rates updated successfully");

        } catch (Exception e) {
            apiFetchLogPort.logError(
                    e.getMessage() != null ? e.getMessage() : "Unknown error"
            );
        }
    }
}
