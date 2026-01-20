package macieserafin.pjwstk.edu.pl.currencymonitorapp.api.exception;

public class ExchangeRateNotFoundException extends RuntimeException {

    public ExchangeRateNotFoundException(String code) {
        super("Exchange rate not found for currency: " + code);
    }
}
