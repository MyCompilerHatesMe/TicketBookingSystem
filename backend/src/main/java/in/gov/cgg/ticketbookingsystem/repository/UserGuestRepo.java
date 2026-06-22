package in.gov.cgg.ticketbookingsystem.repository;

import in.gov.cgg.ticketbookingsystem.model.users.UserGuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGuestRepo extends JpaRepository<UserGuest, Long> {
    Optional<UserGuest> findByEmail(String email);
}
