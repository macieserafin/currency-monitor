package macieserafin.pjwstk.edu.pl.currencymonitorapp;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.usecase.UpdateExchangeRatesUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NbpScheduler {

    private final UpdateExchangeRatesUseCase updateExchangeRatesUseCase;

    public NbpScheduler(UpdateExchangeRatesUseCase updateExchangeRatesUseCase) {
        this.updateExchangeRatesUseCase = updateExchangeRatesUseCase;
    }

    //    @Scheduled(cron = "0 0 * * * *") // co godzinę
    @Scheduled(fixedDelay = 30000) // co 30s
    public void fetchRates() {
        updateExchangeRatesUseCase.update();
    }
}
