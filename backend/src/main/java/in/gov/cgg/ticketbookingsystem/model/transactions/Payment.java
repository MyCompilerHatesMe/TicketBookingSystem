package in.gov.cgg.ticketbookingsystem.model.transactions;

import in.gov.cgg.ticketbookingsystem.model.PaymentMode;
import in.gov.cgg.ticketbookingsystem.model.SimpleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

// TODO: Integration team to complete payment gateway integration and entity definitions
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name="booking_id")
    private Booking booking;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String gatewayMethod; //raw string populated, or not, by the payment gateway

    @Enumerated(EnumType.STRING)
    private SimpleStatus status;

}
