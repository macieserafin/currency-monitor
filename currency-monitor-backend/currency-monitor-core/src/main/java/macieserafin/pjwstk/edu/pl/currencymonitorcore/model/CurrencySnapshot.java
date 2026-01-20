package macieserafin.pjwstk.edu.pl.currencymonitorcore.model;

public class CurrencySnapshot {

    private final String code;
    private final String name;

    public CurrencySnapshot(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
