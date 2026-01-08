package macieserafin.pjwstk.edu.pl.currencymonitor;

import macieserafin.pjwstk.edu.pl.currencymonitor.currency.Currency;
import macieserafin.pjwstk.edu.pl.currencymonitor.currency.CurrencyRepository;
import macieserafin.pjwstk.edu.pl.currencymonitor.currency.ExchangeRate;
import macieserafin.pjwstk.edu.pl.currencymonitor.currency.ExchangeRateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.time.LocalDate;

@EnableScheduling
@EnableCaching
@SpringBootApplication
public class CurrencyMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyMonitorApplication.class, args);
    }



}
