package in.gov.cgg.ticketbookingsystem.model.users;

import in.gov.cgg.ticketbookingsystem.model.Role;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @Column(unique = true, nullable = false)
    private String username; // or email

    @Column(nullable = false)
    private String password;

    @OneToOne
    @JoinColumn(name = "userId")
    private UserMaster userMaster;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "auth_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;
}
