package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.exception.UsernameExistsException;
import in.gov.cgg.ticketbookingsystem.model.Role;
import in.gov.cgg.ticketbookingsystem.model.dto.request.LoginRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.RegisterRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.RegisterResponse;
import in.gov.cgg.ticketbookingsystem.model.users.AuthUser;
import in.gov.cgg.ticketbookingsystem.model.users.UserMaster;
import in.gov.cgg.ticketbookingsystem.repository.AuthUserRepo;
import in.gov.cgg.ticketbookingsystem.security.SecurityUser;
import in.gov.cgg.ticketbookingsystem.utility.DtoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthUserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final DtoMapper mapper;
    private final JwtService jwtService;

    //basic auth. change to JWT/OAuth2
    @Transactional
    public RegisterResponse register (RegisterRequest request) throws IllegalArgumentException {
        AuthUser user = new AuthUser();

        user.setRoles(
                request.roles().stream()
                        .map(s -> {
                            if (s.equalsIgnoreCase(Role.ROLE_ADMIN.name()))
                                throw new IllegalArgumentException("Invalid Role");
                            return Role.valueOf(s);
                        })
                        .collect(Collectors.toSet())
        );

        user.setPassword(passwordEncoder.encode(request.password()));
        user.setUsername(request.username());

        UserMaster userMaster = new UserMaster();
        userMaster.setName(request.name());
        userMaster.setEmail(request.email());
        userMaster.setNumber(request.number());
        userMaster.setCreatedOn(java.time.LocalDateTime.now());
        userMaster.setAuthUser(user);

        user.setUserMaster(userMaster);

        AuthUser saved;
        try {
            saved = userRepo.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UsernameExistsException(user.getUsername());
        }

        return mapper.toResponse(saved);
    }

    public String login (LoginRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            return jwtService.generateToken(
                    request.username(),
                    // authUser will never be null, auth guarantees a "fully authenticated object"
                    // or AuthenticationException.
                    ((SecurityUser) auth.getPrincipal()).authUser().getRoles()
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid Credentials");
        }
    }
}
