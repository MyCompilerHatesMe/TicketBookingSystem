package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.repository.TripSeatRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockReleaseScheduler {

    private final TripSeatRepo tripSeatRepo;

    @Scheduled(fixedRate = 5 * 60 * 1000) // Run every 5 minutes
    @Transactional
    public void releaseExpiredLocks() {
        log.info("Running scheduled job: releaseExpiredLocks...");
        try {
            tripSeatRepo.releaseExpiredLocks(LocalDateTime.now());
            log.info("Expired locks successfully released.");
        } catch (Exception e) {
            log.error("Failed to release expired seat locks: {}", e.getMessage(), e);
        }
    }
}
