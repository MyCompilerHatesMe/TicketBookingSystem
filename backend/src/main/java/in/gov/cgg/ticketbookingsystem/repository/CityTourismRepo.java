package in.gov.cgg.ticketbookingsystem.repository;

import in.gov.cgg.ticketbookingsystem.model.operations.CityTourismData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityTourismRepo extends JpaRepository<CityTourismData, Long> {
    Optional<CityTourismData> findByCityIgnoreCase(String city);
}
