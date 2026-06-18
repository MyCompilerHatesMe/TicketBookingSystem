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
public class Bus {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long busId;

    @Column(nullable = false)
    private String busNumber;

    @Column(nullable = false)
    private String busName;

    @Column(nullable = false)
    private String busType;

    @Column(nullable = false)
    private Integer totalSeats;
}
