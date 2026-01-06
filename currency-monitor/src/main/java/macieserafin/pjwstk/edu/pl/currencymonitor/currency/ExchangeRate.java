package macieserafin.pjwstk.edu.pl.currencymonitor.currency;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "exchange_rates",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"currency_id", "date"})
        }
)
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal rate;

    @Column(nullable = false)
    private LocalDate date;

    //wiele kursów do jednej waluty
    @ManyToOne(optional = false, fetch = FetchType.LAZY) //fetch = FetchType.LAZY -> waluta nie jest dociagana automatycznie
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    protected ExchangeRate() {}

    public ExchangeRate(Currency currency, BigDecimal rate, LocalDate date) {
        this.currency = currency;
        this.rate = rate;
        this.date = date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Long getId() {
        return id;
    }
}
