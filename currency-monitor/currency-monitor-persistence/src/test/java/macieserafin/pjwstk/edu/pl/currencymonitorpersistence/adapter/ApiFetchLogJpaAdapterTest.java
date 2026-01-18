package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.adapter;

import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.ApiFetchLog;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.ApiFetchLogRepository;
import org.testng.annotations.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ApiFetchLogJpaAdapterTest {

    @Test
    void shouldSaveSuccessLog() {
        ApiFetchLogRepository repo = mock(ApiFetchLogRepository.class);
        ApiFetchLogJpaAdapter adapter = new ApiFetchLogJpaAdapter(repo);

        adapter.logSuccess("OK");

        ArgumentCaptor<ApiFetchLog> captor = ArgumentCaptor.forClass(ApiFetchLog.class);
        verify(repo).save(captor.capture());

        assertThat(captor.getValue().getStatus()).isEqualTo("SUCCESS");
        assertThat(captor.getValue().getMessage()).isEqualTo("OK");
    }

    @Test
    void shouldSaveErrorLog() {
        ApiFetchLogRepository repo = mock(ApiFetchLogRepository.class);
        ApiFetchLogJpaAdapter adapter = new ApiFetchLogJpaAdapter(repo);

        adapter.logError("FAIL");

        ArgumentCaptor<ApiFetchLog> captor = ArgumentCaptor.forClass(ApiFetchLog.class);
        verify(repo).save(captor.capture());

        assertThat(captor.getValue().getStatus()).isEqualTo("ERROR");
        assertThat(captor.getValue().getMessage()).isEqualTo("FAIL");
    }
}