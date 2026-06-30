package in.gov.cgg.ticketbookingsystem.model.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeStopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private String stopName;

    @Column(nullable = false)
    private String stopType; // BOARDING or DROPPING

    @Column(nullable = false)
    private Integer minutesOffset;

    @Column(nullable = false)
    private Integer sequence;
}
