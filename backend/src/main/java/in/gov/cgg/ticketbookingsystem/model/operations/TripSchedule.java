package in.gov.cgg.ticketbookingsystem.model.operations;

import in.gov.cgg.ticketbookingsystem.model.core.Bus;
import in.gov.cgg.ticketbookingsystem.model.core.Route;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TripSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @ManyToOne
    @JoinColumn(name="bus_id")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    private LocalDateTime startTime;
    private LocalDateTime arrivalTime;

    private BigDecimal fare;
}
