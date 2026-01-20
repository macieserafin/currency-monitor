package macieserafin.pjwstk.edu.pl.currencymonitorapp.api.exception;

public class CurrencyNotFoundException extends RuntimeException {

    public CurrencyNotFoundException(String code) {
        super("Currency not found: " + code);
    }
}
