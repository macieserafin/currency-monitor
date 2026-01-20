package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.adapter;

import macieserafin.pjwstk.edu.pl.currencymonitorcore.port.ApiFetchLogPort;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.ApiFetchLog;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.ApiFetchLogRepository;
import org.springframework.stereotype.Component;

@Component
public class ApiFetchLogJpaAdapter implements ApiFetchLogPort {

    private final ApiFetchLogRepository repository;

    public ApiFetchLogJpaAdapter(ApiFetchLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void logSuccess(String message) {
        repository.save(new ApiFetchLog("SUCCESS", message));
    }

    @Override
    public void logError(String message) {
        repository.save(new ApiFetchLog("ERROR", message));
    }
}
