package in.gov.cgg.ticketbookingsystem.model.operations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "city_tourism_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityTourismData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String city;

    private Double lat;
    private Double lon;

    @Column(columnDefinition = "TEXT")
    private String placesData;

    private LocalDateTime lastUpdated;
}
