package in.gov.cgg.ticketbookingsystem.repository;

import in.gov.cgg.ticketbookingsystem.model.transactions.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
}
