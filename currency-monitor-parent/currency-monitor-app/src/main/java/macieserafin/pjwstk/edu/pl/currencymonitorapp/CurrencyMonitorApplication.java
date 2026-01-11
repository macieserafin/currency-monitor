package macieserafin.pjwstk.edu.pl.currencymonitorapp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "macieserafin.pjwstk.edu.pl")
@EnableJpaRepositories(basePackages = "macieserafin.pjwstk.edu.pl")
@EntityScan(basePackages = "macieserafin.pjwstk.edu.pl")
@EnableScheduling
@EnableCaching
public class CurrencyMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyMonitorApplication.class, args);
    }



}
