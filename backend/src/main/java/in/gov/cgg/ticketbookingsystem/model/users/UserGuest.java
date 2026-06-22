package in.gov.cgg.ticketbookingsystem.model.users;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String number;

}
