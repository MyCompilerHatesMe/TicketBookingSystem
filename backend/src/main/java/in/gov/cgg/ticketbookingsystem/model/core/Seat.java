package in.gov.cgg.ticketbookingsystem.model.core;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_bus_seat", columnNames = {"busId", "seatNumber"})
        }
)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @Column(nullable = false)
    private String seatNumber;

    @ManyToOne
    @JoinColumn(name="busId", nullable = false)
    private Bus bus;
}
