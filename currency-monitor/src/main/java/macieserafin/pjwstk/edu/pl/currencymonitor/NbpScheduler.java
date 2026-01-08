package macieserafin.pjwstk.edu.pl.currencymonitor;

import macieserafin.pjwstk.edu.pl.currencymonitor.currency.ExchangeRateUpdateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NbpScheduler {

    private final ExchangeRateUpdateService service;

    public NbpScheduler(ExchangeRateUpdateService service) {
        this.service = service;
    }

//    @Scheduled(cron = "0 0 * * * *") // co godzinee
    @Scheduled(fixedDelay = 30000) // co 30s
    public void fetchRates() {
        service.updateRates();
    }
}
