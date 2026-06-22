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
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @Column(nullable = false)
    private String sourceCity;

    @Column(nullable = false)
    private String destinationCity;

    @Column(nullable = false)
    private Integer distanceKm;
}
