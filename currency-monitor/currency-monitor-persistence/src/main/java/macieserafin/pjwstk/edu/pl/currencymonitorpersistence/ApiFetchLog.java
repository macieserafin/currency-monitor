package macieserafin.pjwstk.edu.pl.currencymonitorpersistence;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
