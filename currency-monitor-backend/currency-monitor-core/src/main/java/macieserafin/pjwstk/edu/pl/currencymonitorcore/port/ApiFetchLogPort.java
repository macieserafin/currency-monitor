package macieserafin.pjwstk.edu.pl.currencymonitorcore.port;

public interface ApiFetchLogPort {

    void logSuccess(String message);

    void logError(String message);
}
