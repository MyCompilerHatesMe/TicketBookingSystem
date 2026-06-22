package in.gov.cgg.ticketbookingsystem.model.operations;

import in.gov.cgg.ticketbookingsystem.model.SeatStatus;
import in.gov.cgg.ticketbookingsystem.model.core.Seat;
import in.gov.cgg.ticketbookingsystem.model.transactions.Booking;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "trip_seats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_trip_seat", columnNames = {"trip_id", "seat_id"})
        }
)
public class TripSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private TripSchedule tripSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    private LocalDateTime lockExpiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
}