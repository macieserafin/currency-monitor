package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency;

import jakarta.persistence.*;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.user.User;


import java.time.LocalDateTime;

@Entity
@Table(
        name="observed_currencies",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "currency_id"})
        }
)
public class ObservedCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected ObservedCurrency() {
        // JPA
    }

    public ObservedCurrency(User user, Currency currency) {
        this.user = user;
        this.currency = currency;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Currency getCurrency() {
        return currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
