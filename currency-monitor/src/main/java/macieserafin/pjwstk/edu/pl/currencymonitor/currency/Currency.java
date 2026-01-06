package macieserafin.pjwstk.edu.pl.currencymonitor.currency;

import jakarta.persistence.*;
//klasa mapowana na tabelę
@Entity
@Table(
        //nazwa tabeli
        name = "currencies",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "code") //nie może być dwóch walut o tym samym kodzie
        }
)

public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 3)
    private String code;

    @Column(nullable = false)
    private String name;

    protected Currency() {}

    public Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
