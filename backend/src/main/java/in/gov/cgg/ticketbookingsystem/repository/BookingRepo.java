package in.gov.cgg.ticketbookingsystem.repository;

import in.gov.cgg.ticketbookingsystem.model.transactions.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    // ponytail: query bookings by UserMaster ID
    List<Booking> findByUserMasterUserId(Long userId);

    // ponytail: query bookings by UserGuest ID
    List<Booking> findByUserGuestGuestId(Long guestId);

    // ponytail: update expired booking status to FAILED
    @Modifying
    @Query("UPDATE Booking b SET b.status = 'FAILED' WHERE b.status = 'PENDING' AND b.expiryTime < :now")
    void failExpiredBookings(@Param("now") LocalDateTime now);
}
