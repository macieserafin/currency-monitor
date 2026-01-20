package macieserafin.pjwstk.edu.pl.currencymonitornbpclient;

import macieserafin.pjwstk.edu.pl.currencymonitornbpclient.dto.NbpTableDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class NbpClientTest {

    @Test
    void shouldFetchCurrentRatesFromNbpApi() {
        // given
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        NbpClient client = new NbpClient(restTemplate);

        NbpTableDto[] response = new NbpTableDto[]{new NbpTableDto()};

        when(restTemplate.getForObject(
                "https://api.nbp.pl/api/exchangerates/tables/A?format=json",
                NbpTableDto[].class
        )).thenReturn(response);

        // when
        NbpTableDto[] result = client.fetchCurrentRates();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }
}