package in.gov.cgg.ticketbookingsystem.repository;

import in.gov.cgg.ticketbookingsystem.model.core.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepo extends JpaRepository<Route, Long> {
}
