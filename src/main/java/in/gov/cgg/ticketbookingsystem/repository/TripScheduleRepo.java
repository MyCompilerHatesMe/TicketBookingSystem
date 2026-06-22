package in.gov.cgg.ticketbookingsystem.repository;

import in.gov.cgg.ticketbookingsystem.model.operations.TripSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TripScheduleRepo extends JpaRepository<TripSchedule, Long> {

    @Query("SELECT t FROM TripSchedule t WHERE " +
           "(cast(:sourceCity as string) IS NULL OR t.route.sourceCity = :sourceCity) AND " +
           "(cast(:destinationCity as string) IS NULL OR t.route.destinationCity = :destinationCity) AND " +
           "(cast(:startOfDay as timestamp) IS NULL OR t.startTime >= :startOfDay) AND " +
           "(cast(:endOfDay as timestamp) IS NULL OR t.startTime < :endOfDay)")
    List<TripSchedule> findTripsWithOptionalFilters(
        @Param("sourceCity") String sourceCity,
        @Param("destinationCity") String destinationCity,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );
}
