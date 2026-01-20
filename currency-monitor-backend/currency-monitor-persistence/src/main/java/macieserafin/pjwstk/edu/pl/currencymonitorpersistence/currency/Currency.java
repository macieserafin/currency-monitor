package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency;

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

    public Currency() {}

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

    public void setId(long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }
}
