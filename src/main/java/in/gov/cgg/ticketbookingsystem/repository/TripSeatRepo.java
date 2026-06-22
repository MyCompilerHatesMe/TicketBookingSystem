package in.gov.cgg.ticketbookingsystem.repository;

import in.gov.cgg.ticketbookingsystem.model.operations.TripSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripSeatRepo extends JpaRepository<TripSeat, Long> {

    List<TripSeat> findByTripScheduleTripId(Long tripId);

    @Modifying
    @Query("UPDATE TripSeat t SET t.status = 'AVAILABLE', t.lockExpiresAt = null, t.booking = null " +
           "WHERE t.status = 'PENDING' AND t.lockExpiresAt < :now")
    void releaseExpiredLocks(@Param("now") LocalDateTime now);
}