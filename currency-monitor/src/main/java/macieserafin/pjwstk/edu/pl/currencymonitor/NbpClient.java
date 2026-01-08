package macieserafin.pjwstk.edu.pl.currencymonitor;

import macieserafin.pjwstk.edu.pl.currencymonitor.dto.NbpTableDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NbpClient {

    private static final String NBP_URL =
            "https://api.nbp.pl/api/exchangerates/tables/A?format=json";

    private final RestTemplate restTemplate;

    public NbpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NbpTableDto[] fetchCurrentRates() {
        return restTemplate.getForObject(NBP_URL, NbpTableDto[].class);
    }
}
