package most.qms.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

import static java.time.LocalDate.now;

@Entity
@Table(name = "daily_counter")
@Data
public class DailyCounter
{
    @Id
    @Column(name = "date")
    private LocalDate date = now();

    @Column(name = "counter", nullable = false)
    private Long counter = 1L;

    public void increment()
    {
        counter++;
    }
}
