package macieserafin.pjwstk.edu.pl.currencymonitor;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_fetch_logs")
public class ApiFetchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    private String status;

    @Column(length = 500)
    private String message;

    protected ApiFetchLog() {}

    public ApiFetchLog(String status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }
}
