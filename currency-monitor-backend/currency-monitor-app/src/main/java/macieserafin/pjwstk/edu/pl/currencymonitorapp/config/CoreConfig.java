package macieserafin.pjwstk.edu.pl.currencymonitorapp.config;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ApiFetchLogPort;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.CurrencyPort;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRatePort;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRateProvider;
import macieserafin.pjwstk.edu.pl.currencymonitorcore.usecase.UpdateExchangeRatesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {

    @Bean
    public UpdateExchangeRatesUseCase updateExchangeRatesUseCase(
            CurrencyPort currencyPort,
            ExchangeRatePort exchangeRatePort,
            ApiFetchLogPort apiFetchLogPort,
            ExchangeRateProvider exchangeRateProvider
    ) {
        return new UpdateExchangeRatesUseCase(
                currencyPort,
                exchangeRatePort,
                apiFetchLogPort,
                exchangeRateProvider
        );
    }
}