package in.gov.cgg.ticketbookingsystem.model.transactions;


import in.gov.cgg.ticketbookingsystem.model.SimpleStatus;
import in.gov.cgg.ticketbookingsystem.model.operations.TripSchedule;
import in.gov.cgg.ticketbookingsystem.model.users.UserGuest;
import in.gov.cgg.ticketbookingsystem.model.users.UserMaster;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Check(name = "chk_user_or_guest",
        constraints = "(user_id IS NOT NULL AND guest_id IS NULL) OR (user_id IS NULL AND guest_id IS NOT NULL)"
)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(unique = true, nullable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserMaster userMaster;

    @ManyToOne
    @JoinColumn(name="guest_id")
    private UserGuest userGuest;

    @ManyToOne
    @JoinColumn(name="trip_id", nullable = false)
    private TripSchedule trip;

    private LocalDateTime bookingTime;
    private BigDecimal total_amount;

    private LocalDateTime expiryTime;

    @Enumerated(EnumType.STRING)
    private SimpleStatus status;

}
