package in.gov.cgg.ticketbookingsystem.repository;

import in.gov.cgg.ticketbookingsystem.model.transactions.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    // ponytail: query bookings by UserMaster ID
    List<Booking> findByUserMasterUserId(Long userId);

    // ponytail: query bookings by UserGuest ID
    List<Booking> findByUserGuestGuestId(Long guestId);
}
