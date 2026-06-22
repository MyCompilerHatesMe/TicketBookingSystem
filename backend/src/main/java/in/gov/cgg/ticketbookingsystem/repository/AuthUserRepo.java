package in.gov.cgg.ticketbookingsystem.repository;

import in.gov.cgg.ticketbookingsystem.model.users.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepo extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByUsername(String username);
}
