package macieserafin.pjwstk.edu.pl.currencymonitornbpclient;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ExchangeRateProvider;
import macieserafin.pjwstk.edu.pl.currencymonitornbpclient.dto.NbpTableDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NbpExchangeRateProvider implements ExchangeRateProvider {

    private final NbpClient nbpClient;

    public NbpExchangeRateProvider(NbpClient nbpClient) {
        this.nbpClient = nbpClient;
    }

    @Override
    public ExchangeRateTable fetchCurrentRates() {
        NbpTableDto table = nbpClient.fetchCurrentRates()[0];

        return new ExchangeRateTable(
                LocalDate.parse(table.getEffectiveDate()),
                table.getRates().stream()
                        .map(r -> new ExchangeRateEntry(
                                r.getCurrency(),
                                r.getCode(),
                                r.getMid()
                        ))
                        .toList()
        );
    }
}
